package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.ResponseImport;
import com.backend.entity.VoucherOrder;
import com.backend.repository.VoucherOrderCustomRepository;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IVoucherOrderService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherServiceImpl implements IVoucherOrderService {

    @Autowired
    private VoucherOrderRepository voucherOrderRepository;

    @Autowired
    private VoucherOrderCustomRepository voucherOrderCustomRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrder> addVoucher(VoucherOrderRequest voucherOrderRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Tạo một UUID mới
        UUID uuid = UUID.randomUUID();

        // Lấy giá trị chuỗi của UUID
        String randomString = uuid.toString();

        // Cắt chuỗi UUID để sử dụng một phần cụ thể, ví dụ: 8 ký tự đầu
        String cutRandomString = randomString.substring(0, 8);

        VoucherOrder voucherHoaDon = new VoucherOrder();
        String result = validateVoucher(voucherOrderRequest);
        if (result != null) {
            return result(result);
        } else {
            try {
                voucherHoaDon.setCode("Voucher" + cutRandomString);
                voucherHoaDon.setName(voucherOrderRequest.getName().trim());
                voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());

                if (voucherOrderRequest.getReduceForm() == 0) {
                    // Nếu reduceForm=0, discountAmount là số tiền giảm giá trực tiếp
                    voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                    voucherHoaDon.setMaximumReductionValue(null); // Không cần thiết nhập khi reduceForm = 0
                } else {
                    BigDecimal discountPercentage = voucherOrderRequest.getDiscountAmount();
                    if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(new BigDecimal(100)) > 0) {
                        return new ServiceResult<>(AppConstant.BAD_REQUEST, "Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100", null);
                    }

                    // Kiểm tra xem maximumReductionValue đã được nhập hay chưa
                    BigDecimal maximumReductionValue = voucherOrderRequest.getMaximumReductionValue();
                    if (maximumReductionValue == null) {
                        return new ServiceResult<>(AppConstant.BAD_REQUEST, "Vui lòng nhập giá trị giảm tối đa", null);
                    }

                    if (voucherOrderRequest.getMaximumReductionValue().compareTo(BigDecimal.ZERO) <= 0) {
                        return new ServiceResult<>(AppConstant.BAD_REQUEST,"Giá trị giảm tối đa phải lớn hơn 0",null);
                    }

                    // Kiểm tra xem maximumReductionValue có lớn hơn minBillValue không
                    if (maximumReductionValue != null) {
                        BigDecimal minBillValue = voucherOrderRequest.getMinBillValue();
                        if (maximumReductionValue.compareTo(minBillValue) >= 0) {
                            return new ServiceResult<>(AppConstant.BAD_REQUEST, "Gía trị giảm tối đa phải nhỏ hơn giá trị hóa đơn tối thiểu", null);
                        }
                    }

                    voucherHoaDon.setDiscountAmount(discountPercentage);
                    voucherHoaDon.setMaximumReductionValue(maximumReductionValue);
                }

                voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                voucherHoaDon.setCreateDate(currentDateTime);
                voucherHoaDon.setUpdateAt(currentDateTime);
                voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());

                // Kiểm tra xem người dùng đã cung cấp giá trị status chưa
                if (voucherOrderRequest.getStatus() != null) {
                    voucherHoaDon.setStatus(voucherOrderRequest.getStatus());
                } else {
                    // Nếu người dùng không cung cấp giá trị status, đặt mặc định là 0
                    voucherHoaDon.setStatus(0);
                }

                voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                return new ServiceResult<>(AppConstant.SUCCESS, "Thêm thành công", voucherHoaDon);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrder> updateVoucher(VoucherOrderRequest voucherOrderRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<VoucherOrder> optionalVoucherHoaDon = voucherOrderRepository.findById(voucherOrderRequest.getId());
        if (optionalVoucherHoaDon.isPresent()) {
            VoucherOrder voucherHoaDon = optionalVoucherHoaDon.get();
            String result = validateVoucher(voucherOrderRequest);
            if (result != null) {
                return result(result);
            } else {
                try {
                    voucherHoaDon.setName(voucherOrderRequest.getName().trim());
                    voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());

                    if (voucherOrderRequest.getReduceForm() == 0) {
                        // Nếu reduceForm=0, discountAmount là số tiền giảm giá trực tiếp
                        voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                        voucherHoaDon.setMaximumReductionValue(null); // Không cần thiết nhập khi reduceForm = 0
                    } else {
                        // Nếu reduceForm=1, discountAmount là phần trăm giảm giá
                        BigDecimal discountPercentage = voucherOrderRequest.getDiscountAmount();
                        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(new BigDecimal(100)) > 0) {
                            return new ServiceResult<>(AppConstant.BAD_REQUEST, "Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100", null);
                        }

                        // Kiểm tra xem maximumReductionValue đã được nhập hay chưa
                        BigDecimal maximumReductionValue = voucherOrderRequest.getMaximumReductionValue();
                        if (maximumReductionValue == null) {
                            return new ServiceResult<>(AppConstant.BAD_REQUEST, "Vui lòng nhập giá trị giảm tối đa", null);
                        }

                        if (voucherOrderRequest.getMaximumReductionValue().compareTo(BigDecimal.ZERO) <= 0) {
                            return new ServiceResult<>(AppConstant.BAD_REQUEST,"Giá trị giảm tối đa phải lớn hơn 0",null);
                        }

                        // Kiểm tra xem maximumReductionValue có lớn hơn minBillValue không
                        if (maximumReductionValue != null) {
                            BigDecimal minBillValue = voucherOrderRequest.getMinBillValue();
                            if (maximumReductionValue.compareTo(minBillValue) >= 0) {
                                return new ServiceResult<>(AppConstant.BAD_REQUEST, "Gía trị giảm tối đa phải nhỏ hơn giá trị hóa đơn tối thiểu", null);
                            }
                        }

                        voucherHoaDon.setDiscountAmount(discountPercentage);
                        voucherHoaDon.setMaximumReductionValue(maximumReductionValue);

                    }
                    voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                    voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                    voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                    voucherHoaDon.setUpdateAt(currentDateTime);
                    voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());

                    //Kiểm tra xem người dùng đã cung cấp giá trị status chưa
                    if (voucherOrderRequest.getStatus() != null) {
                        voucherHoaDon.setStatus(voucherOrderRequest.getStatus());
                    } else {
                        // Nếu người dùng không cung cấp giá trị status, đặt mặc định là 0
                        voucherHoaDon.setStatus(0);
                    }

                    voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                    //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                    return new ServiceResult<>(AppConstant.SUCCESS, "Sửa thành công", voucherHoaDon);
                } catch (Exception e) {
                    // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
                }
            }
        } else {
            return new ServiceResult<>(AppConstant.FAIL, "Id không tồn tại", null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrder> updateStatusVoucherCancelFromWait(VoucherOrderRequest voucherOrderRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<VoucherOrder> optionalVoucherHoaDon = voucherOrderRepository.findById(voucherOrderRequest.getId());
        if (optionalVoucherHoaDon.isPresent()) {
            VoucherOrder voucherHoaDon = optionalVoucherHoaDon.get();
            try {
                if (voucherHoaDon.getStatus() == 3) {
                    voucherHoaDon.setUpdateAt(currentDateTime);
                    voucherHoaDon.setStatus(0);
                    voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                    return new ServiceResult<>(AppConstant.SUCCESS, "Cập nhật trạng thái voucher thành công, vui lòng sửa lại các thông tin nếu cần!", voucherHoaDon);
                } else {
                    throw new RuntimeException("Trạng thái không hợp lệ");
                }
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                //return new ServiceResult<>(AppConstant.SUCCESS, "Cập nhật trạng thái voucher thành công", voucherHoaDon);
            } catch (Exception e) {
                // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
            }

        } else {
            return new ServiceResult<>(AppConstant.FAIL, "Id không tồn tại", null);
        }
    }

    @Override
    @Scheduled(fixedRate = 1800000)
    public void updateVoucherStatus() {
        List<VoucherOrder> vouchers = voucherOrderRepository.findAll();
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (VoucherOrder voucher : vouchers) {
            if (voucher.getStartDate() == null || voucher.getEndDate() == null) {
                // Bỏ qua voucher này nếu có startDate hoặc endDate là null
                continue;
            }
            if (voucher.getQuantity() == 0) {
                voucher.setStatus(2); // Cập nhật thành "hết hạn"
            } else {
                if (voucher.getStatus() != 3) { //status=3: khi xoá đổi trạng thái thành ẩn
                    // Chỉ cập nhật status nếu status hiện tại không phải là 3
                    if (currentDateTime.isAfter(voucher.getStartDate()) && currentDateTime.isBefore(voucher.getEndDate())) {
                        voucher.setStatus(1); // Cập nhật thành "đã kích hoạt"
                    } else if (currentDateTime.isAfter(voucher.getEndDate())) {
                        voucher.setStatus(2); // Cập nhật thành "hết hạn"
                    } else {
                        voucher.setStatus(0); // Cập nhật thành chờ kích hoạt
                    }
                }
            }
            voucherOrderRepository.save(voucher);
        }
    }

    @Override
    public VoucherOrderResponse convertPage(Object[] object) {
        VoucherOrderResponse voucherOrderResponse = new VoucherOrderResponse();
        voucherOrderResponse.setId(((BigInteger) object[0]).longValue());
        voucherOrderResponse.setCode((String) object[1]);
        voucherOrderResponse.setName((String) object[2]);
        voucherOrderResponse.setQuantity((Integer) object[3]);
        voucherOrderResponse.setMinBillValue((BigDecimal) object[4]);
        voucherOrderResponse.setDiscountAmount((BigDecimal) object[5]);
        voucherOrderResponse.setMaximumReductionValue((BigDecimal) object[6]);

        if (object[7] != null) {
            Timestamp startDateTimestamp = (Timestamp) object[7];
            voucherOrderResponse.setStartDate(startDateTimestamp.toLocalDateTime());
        }

        if (object[8] != null) {
            Timestamp endDateTimestamp = (Timestamp) object[8];
            voucherOrderResponse.setEndDate(endDateTimestamp.toLocalDateTime());
        }

        if (object[9] != null) {
            Timestamp createDateTimestamp = (Timestamp) object[9];
            voucherOrderResponse.setCreateDate(createDateTimestamp.toLocalDateTime());
        }

        if (object[10] != null) {
            Timestamp updateAtTimestamp = (Timestamp) object[10];
            voucherOrderResponse.setUpdateAt(updateAtTimestamp.toLocalDateTime());
        }

        voucherOrderResponse.setReduceForm((Integer) object[11]);
        voucherOrderResponse.setStatus((Integer) object[12]);
        return voucherOrderResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrder> deleteVoucher(VoucherOrderRequest voucherOrderRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<VoucherOrder> optionalVoucherHoaDon = voucherOrderRepository.findById(voucherOrderRequest.getId());
        if (optionalVoucherHoaDon.isPresent()) {
            VoucherOrder voucherHoaDon = optionalVoucherHoaDon.get();
            try {
                if (voucherHoaDon.getStatus() == 0) {
                    voucherHoaDon.setUpdateAt(currentDateTime);
                    voucherHoaDon.setStatus(3);
                    voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                } else if (voucherHoaDon.getStatus() == 1) {
                    throw new RuntimeException("Voucher đang kích hoạt và không thể chuyển sang huỷ kích hoạt");
                } else if (voucherHoaDon.getStatus() == 2) {
                    throw new RuntimeException("Voucher đã hết hạn và không thể chuyển sang huỷ kích hoạt");
                } else {
                    throw new RuntimeException("Trạng thái không hợp lệ");
                }
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                return new ServiceResult<>(AppConstant.SUCCESS, "Huỷ kích hoạt voucher thành công", voucherHoaDon);
            } catch (Exception e) {
                // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
            }

        } else {
            return new ServiceResult<>(AppConstant.FAIL, "Id không tồn tại", null);
        }
    }

    @Override
    public ServiceResult<VoucherOrder> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL, mess, null);
    }

    @Override
    public String validateVoucher(VoucherOrderRequest voucherOrderRequest) {
        List<String> errorMessages = new ArrayList<>();

        //Validate trống
//        if (voucherOrderRequest.getCode() == null || (voucherOrderRequest.getCode() != null && voucherOrderRequest.getCode().trim().isEmpty())) {
//            errorMessages.add("Code không được để trống");
//        }
        if (voucherOrderRequest.getName() == null || (voucherOrderRequest.getName() != null && voucherOrderRequest.getName().trim().isEmpty())){
            errorMessages.add("Tên voucher không được để trống");
        }
        if (voucherOrderRequest.getName() != null && voucherOrderRequest.getName().length() > 255) {
            errorMessages.add("Tên voucher không được quá 255 ký tự");
        }
        if (voucherOrderRequest.getQuantity() == null) {
            errorMessages.add("Số lượng không được để trống");
        }
        if (voucherOrderRequest.getDiscountAmount() == null) {
            errorMessages.add("Giá trị giảm không được để trống");
        }
        if (voucherOrderRequest.getMinBillValue() == null) {
            errorMessages.add("Giá trị hoá đơn tối thiểu không được để trống");
        }
        if (voucherOrderRequest.getStartDate() == null) {
            errorMessages.add("Ngày bắt đầu không được để trống");
        }
        if (voucherOrderRequest.getEndDate() == null) {
            errorMessages.add("Ngày kết thúc không được để trống");
        }
        if (voucherOrderRequest.getReduceForm() == null) {
            errorMessages.add("Hình thức giảm không được để trống");
        }

        // Kiểm tra số lượng
        if (voucherOrderRequest.getQuantity() != null && voucherOrderRequest.getQuantity() <= 0) {
            errorMessages.add("Số lượng phải lớn hơn 0");
        }

//        if (voucherOrderRequest.getQuantity() != null && (voucherOrderRequest.getQuantity() < 1 || voucherOrderRequest.getQuantity() > 100)) {
//            errorMessages.add("Quantity phải nằm trong khoảng từ 1 đến 100");
//        }
        if (voucherOrderRequest.getDiscountAmount() != null && voucherOrderRequest.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errorMessages.add("Giá trị giảm phải lớn hơn 0");
        }
        if (voucherOrderRequest.getMinBillValue() != null && voucherOrderRequest.getMinBillValue().compareTo(BigDecimal.ZERO) <= 0) {
            errorMessages.add("Giá trị hoá đơn tối thiểu phải lớn hơn 0");
        }

        //Kiểm tra trạng thái và hình thức giảm
        if (voucherOrderRequest.getReduceForm() != null && (voucherOrderRequest.getReduceForm() < 0 || voucherOrderRequest.getReduceForm() > 1)) {
            errorMessages.add("Hình thức giảm phải là phần trăm hoặc tiền");
        }
        if (voucherOrderRequest.getStatus() != null && (voucherOrderRequest.getStatus() < 0 || voucherOrderRequest.getStatus() > 2)) {
            errorMessages.add("Trạng thái phải nằm trong khoảng từ 0 đến 2");
        }

//        if (voucherOrderRequest.getMaximumReductionValue().compareTo(voucherOrderRequest.getMinBillValue()) > 0) {
//            // Giá trị giảm tối đa không được lớn hơn giá trị hoá đơn tối thiểu
//            errorMessages.add("Giá trị giảm tối đa không được lớn hơn giá trị hoá đơn tối thiểu");
//        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (voucherOrderRequest.getStartDate() != null && voucherOrderRequest.getEndDate() != null) {
            if (voucherOrderRequest.getEndDate().isBefore(voucherOrderRequest.getStartDate())) {
                errorMessages.add("Ngày kết thúc phải sau ngày bắt đầu");
            }
            if (voucherOrderRequest.getStartDate().toLocalDate().isBefore(LocalDate.now())) {
                errorMessages.add("Ngày bắt đầu phải sau ngày hiện tại");
            }
            if (voucherOrderRequest.getEndDate().toLocalDate().isBefore(LocalDate.now())) {
                errorMessages.add("Ngày kết thúc phải sau ngày hiện tại");
            }
        }
        if (voucherOrderRequest.getCreateDate() != null && voucherOrderRequest.getUpdateAt() != null) {
            if (voucherOrderRequest.getStartDate().isBefore(voucherOrderRequest.getCreateDate())) {
                errorMessages.add("Ngày bắt đầu phải trước ngày kết thúc");
            }
        }

        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    @Override
    public Page<VoucherOrderResponse> searchVoucher(VoucherOrderRequest voucherOrderRequest) {
        Pageable pageable = PageRequest.of(voucherOrderRequest.getPage() - 1, voucherOrderRequest.getSize());
        if (voucherOrderRequest.getName() != null) {
            String name = voucherOrderRequest.getName();
            name = name.replaceAll("\\\\", "\\\\\\");
            name = name.replaceAll("%", "\\\\\\%");
            name = name.replaceAll("_", "\\\\\\_");
            voucherOrderRequest.setName(name);
        }
        Page<Object> objects = voucherOrderCustomRepository.doSearch(
                pageable,
                voucherOrderRequest.getName(),
                voucherOrderRequest.getStatus(),
                voucherOrderRequest.getStartDate(),
                voucherOrderRequest.getEndDate()
        );

        List<VoucherOrderResponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            VoucherOrderResponse voucherOrderResponse = convertPage(result);
            list.add(voucherOrderResponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }

    @Override
    public ServiceResultReponse<VoucherOrder> getOne(String code) {
        Optional<VoucherOrder> voucherOrder = voucherOrderRepository.findVoucherByCode(code);
        if (voucherOrder.isPresent()) {
            VoucherOrder voucherOrderGet = voucherOrder.get();
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, voucherOrderGet, "Đã tìm thấy voucher");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã voucher không tồn tại");
        }
    }

    @Override
    public List<VoucherOrderResponse> searchTotalMoneyMyOrder(VoucherOrderRequest voucherOrderRequest) {

        List<Object> objects = voucherOrderCustomRepository.doSearchMinBillValue(
                voucherOrderRequest.getTotalMoneyMyOrder()
        );

        List<VoucherOrderResponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            VoucherOrderResponse voucherOrderResponse = convertPage(result);
            list.add(voucherOrderResponse);
        }
        return list;
    }

    @Override
    public byte[] createExcelFile() throws IOException {
        // Đường dẫn tới tệp Excel mẫu
        String excelResourcePath = "/static/fileMau/fileMauVoucher.xlsx";
//        String excelResourcePath = "/static/fileMau/fileMauVoucherTest.xlsm";

        // Đọc tệp Excel mẫu từ tài nguyên
        Resource resource = new ClassPathResource(excelResourcePath);
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();

        // Lấy danh sách reduce
        List<Integer> reduce = voucherOrderRepository.listAllByReduce();
        // Tạo sheet riêng cho reduce
        Sheet reduceSheet = workbook.createSheet("ReduceSheet");
        // Đổ dữ liệu reduce vào sheet ReduceSheet
        int rowIndexReduce = 0;
        for (int j = 0; j < reduce.size(); j++) {
            Row row = reduceSheet.createRow(rowIndexReduce++);
            row.createCell(0).setCellValue(reduce.get(j));
        }
        Sheet sheet = workbook.getSheet("Sheet1");
        int maxRows = 1000;
        for (int i = 1; i <= maxRows; i++) {
            Row currentRow = sheet.createRow(i);
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            // Ràng buộc dữ liệu cho hình thức giảm(Cột G) sử dụng giá trị từ reduce
            CellRangeAddressList addressListType = new CellRangeAddressList(i, i, 6, 6); // Cột G
            DataValidationConstraint dvConstraintType = dvHelper.createFormulaListConstraint("ReduceSheet!$A$1:$A$" + rowIndexReduce);
            DataValidation validationType = dvHelper.createValidation(dvConstraintType, addressListType);
            validationType.setShowErrorBox(true);
            validationType.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationType.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách hình thức giảm.");
            sheet.addValidationData(validationType);
        }
        workbook.setSheetHidden(workbook.getSheetIndex("ReduceSheet"), true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    @Override
    public VoucherOrderResponse createVoucherResponse(Row row, List<String> errors, List<VoucherOrderResponse> voucherListError, Integer type) {
        Cell codeCell = row.getCell(1);
        Cell nameCell = row.getCell(2);
        Cell quantityCell = row.getCell(3);
        Cell minBillValueCell = row.getCell(4);
        Cell discountAmountCell = row.getCell(5);
        Cell reduceFormCell = row.getCell(6);
        Cell maximumReductionValueCell = row.getCell(7);
        Cell startDateCell = row.getCell(8);
        Cell endDateCell = row.getCell(9);

        String code = (codeCell == null) ? " " : codeCell.getStringCellValue();
        String name = (nameCell == null) ? " " : nameCell.getStringCellValue();
        double quantity;
        if (quantityCell == null || quantityCell.getCellType() == CellType.BLANK) {
            quantity = 99999999;
        } else {
            quantity = quantityCell.getNumericCellValue();
        }
        //
        double minBillValue;
        if (minBillValueCell == null || minBillValueCell.getCellType() == CellType.BLANK) {
            minBillValue = 0.1;
        } else {
            minBillValue = minBillValueCell.getNumericCellValue();
        }
        //
        double discountAmount;
        if (discountAmountCell == null || discountAmountCell.getCellType() == CellType.BLANK) {
            discountAmount = 0.1;
        } else {
            discountAmount = discountAmountCell.getNumericCellValue();
        }
        //
        double reduceForm;
        if (reduceFormCell == null || reduceFormCell.getCellType() == CellType.BLANK) {
            reduceForm = 2.0;
        } else {
            reduceForm = reduceFormCell.getNumericCellValue();
        }
        //
        double maximumReductionValue;
        if (maximumReductionValueCell == null || maximumReductionValueCell.getCellType() == CellType.BLANK) {
            maximumReductionValue = 0.1;
        } else {
            maximumReductionValue = maximumReductionValueCell.getNumericCellValue();
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (startDateCell != null && startDateCell.getCellType() == CellType.NUMERIC) {
            Date date = startDateCell.getDateCellValue();
            startDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (startDateCell != null && startDateCell.getCellType() == CellType.STRING) {
            try {
                startDate = LocalDateTime.parse(startDateCell.getStringCellValue(), dateFormatter);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        if (endDateCell != null && endDateCell.getCellType() == CellType.NUMERIC) {
            Date date = endDateCell.getDateCellValue();
            endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (endDateCell != null && endDateCell.getCellType() == CellType.STRING) {
            try {
                endDate = LocalDateTime.parse(endDateCell.getStringCellValue(), dateFormatter);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }


        LocalDateTime currentDateTime = LocalDateTime.now();
        VoucherOrderResponse voucherOrderResponse = new VoucherOrderResponse();
        voucherOrderResponse.setCode(code);
        voucherOrderResponse.setName(name);
        voucherOrderResponse.setQuantity(Integer.valueOf((int) quantity));
        voucherOrderResponse.setDiscountAmount(BigDecimal.valueOf(discountAmount));
        voucherOrderResponse.setMinBillValue(BigDecimal.valueOf(minBillValue));
        voucherOrderResponse.setMaximumReductionValue(BigDecimal.valueOf(maximumReductionValue));
        voucherOrderResponse.setStartDate(startDate);
        voucherOrderResponse.setEndDate(endDate);
        voucherOrderResponse.setCreateDate(currentDateTime);
        try {
            long reduceValue = (long) reduceForm;
            voucherOrderResponse.setReduceForm((int) reduceValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // checkvalidate import
        String errorMessage = "";
        if (nameCell == null || nameCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Tên voucher không được để trống. " + "\n";
        }
        if (codeCell == null || codeCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Mã voucher không được để trống. " + "\n";
        }
        if (codeCell != null && type == 0) {
            if (code.trim().length() > 50) {
                errorMessage += "Mã voucher không được lớn hơn 50. " + "\n";
            } else {
                Optional<VoucherOrder> voucherOrder = voucherOrderRepository.findVoucherByCode(code);
                if (voucherOrder.isPresent()) {
                    errorMessage += "Mã voucher đã tồn tại. " + "\n";
                }
            }
        }
        if (codeCell != null && type == 1) {
            if (code.trim().length() > 50) {
                errorMessage += "Mã voucher không được lớn hơn 50. " + "\n";
            } else {
                Optional<VoucherOrder> voucherOrder = voucherOrderRepository.findVoucherByCode(code);
                if (!voucherOrder.isPresent()) {
                    errorMessage += "Mã voucher không tồn tại. " + "\n";
                }
            }
        }
        if (!name.equals(" ") && name.trim().length() > 250) {
            errorMessage += "Tên voucher không được lớn hơn 250. " + "\n";
        }
        if (quantityCell == null || quantityCell.getCellType() == CellType.BLANK) {
            errorMessage += "Số lượng không được để trống. " + "\n";
        }
        if (quantityCell != null) {
            if (quantity == 0) {
                errorMessage += "Số lượng phải lớn hơn 0 " + "\n";
            }
        }
        if (minBillValueCell == null || minBillValueCell.getCellType() == CellType.BLANK) {
            errorMessage += "Giá trị tối thiểu của hóa đơn không được để trống. " + "\n";
        }
        if (minBillValueCell != null) {
            if (minBillValue == 0) {
                errorMessage += "Giá trị tối thiểu của hóa đơn phải lớn hơn 0 " + "\n";
            }
        }
        if (discountAmountCell == null || discountAmountCell.getCellType() == CellType.BLANK) {
            errorMessage += "Giá trị giảm không được để trống. " + "\n";
        }
        if (discountAmountCell != null) {
            if (discountAmount == 0) {
                errorMessage += "Giá trị giảm phải lớn hơn 0 " + "\n";
            }
        }
        if (reduceFormCell == null || reduceFormCell.getCellType() == CellType.BLANK) {
            errorMessage += "Hình thức giảm không được để trống. " + "\n";
        }
        if (reduceFormCell != null && reduceForm == 1 && (maximumReductionValueCell == null || maximumReductionValueCell.getCellType() == CellType.BLANK)) {
            errorMessage += "Giá trị giảm tối đa không được để trống. " + "\n";
        }
        if (maximumReductionValueCell != null) {
            if (reduceFormCell != null && reduceForm == 1 && maximumReductionValue == 0)
                errorMessage += "Giá trị giảm tối đa phải lớn hơn 0. " + "\n";
        }
        if (startDateCell == null || startDateCell.getCellType() == CellType.BLANK) {
            errorMessage += "Ngày bắt đầu không được để trống. " + "\n";
        }
        if (startDateCell != null) {
            if (startDate.isBefore(currentDateTime)) {
                errorMessage += "Ngày bắt đầu phải lớn hơn ngày hiện tại. " + "\n";
            }
        }
        if (endDateCell == null || endDateCell.getCellType() == CellType.BLANK) {
            errorMessage += "Ngày kết thúc không được để trống. " + "\n";
        }
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                errorMessage += "Ngày kết thúc phải lớn hơn ngày bắt đầu. " + "\n";
            }
        }
        if (!errorMessage.isEmpty()) {
            errors.add(errorMessage);
            voucherListError.add(voucherOrderResponse);
            return null;
        } else {
            // Nếu không có lỗi, trả về đối tượng voucherOrderResponse
            return voucherOrderResponse;
        }
    }

    @Override
    public void saveVoucherOrder(VoucherOrderResponse voucherOrderResponse, Integer type) {
        VoucherOrder voucherOrder = new VoucherOrder();
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (type == 1) {
            // update
            voucherOrder = voucherOrderRepository.findVoucherByCode(voucherOrderResponse.getCode()).get();
            voucherOrder.setUpdateAt(currentDateTime);
        }
        voucherOrder.setName(voucherOrderResponse.getName());
        voucherOrder.setCode(voucherOrderResponse.getCode());
        voucherOrder.setQuantity(voucherOrderResponse.getQuantity());
        voucherOrder.setDiscountAmount(voucherOrderResponse.getDiscountAmount());
        voucherOrder.setMinBillValue(voucherOrderResponse.getMinBillValue());
        voucherOrder.setReduceForm(voucherOrderResponse.getReduceForm());
        voucherOrder.setMaximumReductionValue(voucherOrderResponse.getMaximumReductionValue());
        voucherOrder.setStartDate(voucherOrderResponse.getStartDate());
        voucherOrder.setEndDate(voucherOrderResponse.getEndDate());
        voucherOrder.setCreateDate(currentDateTime);
        voucherOrder.setStatus(0);
        voucherOrderRepository.save(voucherOrder);
    }

    @Override
    public ResponseImport importDataFromExcel(MultipartFile file, Integer type) throws IOException {
        int fail = 0;
        int total = 0;
        List<String> errors = new ArrayList<>();
        List<VoucherOrderResponse> voucherListError = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        // check file trống hoặc có mỗi header
        if (sheet.getPhysicalNumberOfRows() <= 1) {
            total = 0;
        } else {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                // Kiểm tra và xử lý lỗi nếu ô trống hoặc không tồn tại
                VoucherOrderResponse voucherOrderResponse = createVoucherResponse(row, errors, voucherListError, type);

                if (voucherOrderResponse != null) {
                    // Xử lý dữ liệu hợp lệ và lưu voucherRespose vào database
                    saveVoucherOrder(voucherOrderResponse, type);
                }
                total++;
            }
        }
        if (!errors.isEmpty()) {
            String existingExcelFilePath = "src/main/java/com/backend/file/error-file-voucher.xlsx";
            createErrorExcelFile(existingExcelFilePath, errors, voucherListError, type);
//            System.out.println(errors);
        }
        fail = errors.size();
        ResponseImport voucherResponseImport = new ResponseImport();
        voucherResponseImport.setFail(fail);
        voucherResponseImport.setTotal(total);
        voucherResponseImport.setSuccess((total - fail));
        workbook.close();
        return voucherResponseImport;
    }

    @Override
    public void createErrorExcelFile(String existingExcelFilePath, List<String> errors, List<VoucherOrderResponse> voucherListError, Integer type) {
        try (FileInputStream existingExcelFileInputStream = new FileInputStream(existingExcelFilePath)) {
            Workbook errorWorkbook = new XSSFWorkbook(existingExcelFileInputStream);
            Sheet errorSheet = errorWorkbook.getSheet("Sheet1");
            if (errorSheet != null) {
                int lastRowNum = errorSheet.getLastRowNum();
                for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                    Row row = errorSheet.getRow(rowIndex);
                    if (row != null) {
                        errorSheet.removeRow(row);
                    }
                }
            } else {
                errorSheet = errorWorkbook.createSheet("Sheet1");
            }
            Row headerRow = errorSheet.getRow(0);
            if (headerRow == null) {
                headerRow = errorSheet.createRow(0);
            }
            Cell headerCell = headerRow.createCell(10);
            headerCell.setCellValue("Chi tiết lỗi");
            int columnWidth = 40;
            errorSheet.setColumnWidth(10, columnWidth * 256);
            CellStyle headerCellStyle = createHeaderCellStyle(errorWorkbook);
            headerCell.setCellStyle(headerCellStyle);


            // Font cho lỗi
            CellStyle errorCellStyle = createErrorCellStyle(errorWorkbook);

            // Font cho symble
            CellStyle cellSymble = createErrorCellStyleSymble(errorWorkbook);
            // Font cho symble date
            CellStyle cellSymbleDate = createErrorCellStyleDateSymble(errorWorkbook);
            // Border cho lỗi
            CellStyle cellStyle = createBorderCellStyle(errorWorkbook);
            // Border cho lỗi Date
            CellStyle cellStyleDate = createBorderAndFormatDateCellStyle(errorWorkbook);
            if (errorSheet == null) {
                errorSheet = errorWorkbook.createSheet("Sheet1");
            }
            LocalDateTime currentDateTime = LocalDateTime.now();
            int rowIndex = 1;
            for (int i = 0; i < errors.size() || i < voucherListError.size(); i++) {
                Row errorRow = errorSheet.createRow(rowIndex++);

                // Tạo ô dữ liệu từ danh sách errors nếu có
                if (i < errors.size()) {
                    Cell cell = errorRow.createCell(10);
                    cell.setCellValue(errors.get(i));
                    cell.setCellStyle(errorCellStyle);
                }

                // Tạo ô dữ liệu từ danh sách voucherListError nếu có
                if (i < voucherListError.size()) {
                    Cell cellSTT = errorRow.createCell(0);
                    cellSTT.setCellValue(i + 1);
                    cellSTT.setCellStyle(cellSymble);

                    // code
                    Cell cellCode = errorRow.createCell(1);
                    cellCode.setCellValue(voucherListError.get(i).getCode().trim());
                    if (voucherListError.get(i).getCode().equals(" ") || voucherListError.get(i).getCode().trim().isEmpty()) {
                        cellCode.setCellStyle(cellStyle);
                    } else {
                        VoucherOrder voucherOrder = voucherOrderRepository.checkDuplicate(voucherListError.get(i).getCode());
                        if (voucherListError.get(i).getCode().trim().length() > 50) {
                            cellCode.setCellStyle(cellStyle);
                        } else if (voucherOrder != null && type == 0) {
                            cellCode.setCellStyle(cellStyle);
                        } else if (voucherOrder == null && type == 1) {
                            cellCode.setCellStyle(cellStyle);
                        } else {
                            cellCode.setCellStyle(cellSymble);
                        }
                    }
                    // name
                    Cell cellName = errorRow.createCell(2);
                    cellName.setCellValue(voucherListError.get(i).getName().trim());
                    if (voucherListError.get(i).getName().equals(" ") || voucherListError.get(i).getName().trim().isEmpty()) {
                        cellName.setCellStyle(cellStyle);
                    } else if (voucherListError.get(i).getName().trim().length() > 250) {
                        cellName.setCellStyle(cellStyle);
                    } else {
                        cellName.setCellStyle(cellSymble);
                    }
                    // quantity
                    Cell cellQuantity = errorRow.createCell(3);
                    if (voucherListError.get(i).getQuantity() == 99999999) {
                        cellQuantity.setCellValue((Date) null);
                        cellQuantity.setCellStyle(cellStyle);
                    } else if (voucherListError.get(i).getQuantity() == 0) {
                        cellQuantity.setCellValue(voucherListError.get(i).getQuantity());
                        cellQuantity.setCellStyle(cellStyle);
                    } else {
                        cellQuantity.setCellValue(voucherListError.get(i).getQuantity());
                        cellQuantity.setCellStyle(cellSymble);
                    }
                    // minBillValueCell
                    Cell cellMinBillValueCell = errorRow.createCell(4);
                    BigDecimal minBillValueCell = voucherListError.get(i).getMinBillValue();
                    if (minBillValueCell.compareTo(new BigDecimal("0.1")) == 0) {
                        cellMinBillValueCell.setCellValue((Date) null);
                        cellMinBillValueCell.setCellStyle(cellStyle);
                    } else if (minBillValueCell.compareTo(BigDecimal.ZERO) == 0) {
                        cellMinBillValueCell.setCellValue(0);
                        cellMinBillValueCell.setCellStyle(cellStyle);
                    } else {
                        cellMinBillValueCell.setCellValue(minBillValueCell.toString());
                        cellMinBillValueCell.setCellStyle(cellSymble);
                    }
                    // discountAmountCell
                    Cell cellDiscountAmountCell = errorRow.createCell(5);
                    BigDecimal discountAmount = voucherListError.get(i).getDiscountAmount();
                    if (discountAmount.compareTo(new BigDecimal("0.1")) == 0) {
                        cellDiscountAmountCell.setCellValue((Date) null);
                        cellDiscountAmountCell.setCellStyle(cellStyle);
                    } else if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
                        cellDiscountAmountCell.setCellValue(0);
                        cellDiscountAmountCell.setCellStyle(cellStyle);
                    } else {
                        cellDiscountAmountCell.setCellValue(discountAmount.toString());
                        cellDiscountAmountCell.setCellStyle(cellSymble);
                    }
                    // reduce
                    Cell cellType = errorRow.createCell(6);
                    if (voucherListError.get(i).getReduceForm() == 2) {
                        cellType.setCellValue((Date) null);
                        cellType.setCellStyle(cellStyle);
                    } else {
                        cellType.setCellValue(voucherListError.get(i).getReduceForm());
                        cellType.setCellStyle(cellSymble);
                    }
                    // maximumReductionValueCell
                    Cell cellMaximumReductionValueCell = errorRow.createCell(7);
                    BigDecimal maximumReductionValue = voucherListError.get(i).getMaximumReductionValue();
                    if (voucherListError.get(i).getReduceForm() == 1 && maximumReductionValue.compareTo(new BigDecimal("0.1")) == 0) {
                        cellMaximumReductionValueCell.setCellValue((Date) null);
                        cellMaximumReductionValueCell.setCellStyle(cellStyle);
                    } else if (voucherListError.get(i).getReduceForm() == 1 && maximumReductionValue.compareTo(BigDecimal.ZERO) == 0) {
                        cellMaximumReductionValueCell.setCellValue(0);
                        cellMaximumReductionValueCell.setCellStyle(cellSymble);
                    } else if (voucherListError.get(i).getReduceForm() == 1) {
                        cellMaximumReductionValueCell.setCellValue(maximumReductionValue.toString());
                        cellMaximumReductionValueCell.setCellStyle(cellSymble);
                    } else {
                        cellMaximumReductionValueCell.setCellValue((Date) null);
                        cellMaximumReductionValueCell.setCellStyle(cellSymble);
                    }
                    // startDate
                    Cell cellStartDate = errorRow.createCell(8);
                    if (voucherListError.get(i).getStartDate() == null) {
                        cellStartDate.setCellValue((Date) null);
                        cellStartDate.setCellStyle(cellStyle);
                    } else if (voucherListError.get(i).getStartDate().isBefore(currentDateTime)) {
                        cellStartDate.setCellValue(voucherListError.get(i).getStartDate());
                        cellStartDate.setCellStyle(cellStyleDate);
                    } else {
                        cellStartDate.setCellValue(voucherListError.get(i).getStartDate());
                        cellStartDate.setCellStyle(cellSymbleDate);
                    }
                    // endDate
                    Cell cellEndDate = errorRow.createCell(9);
                    if (voucherListError.get(i).getEndDate() == null) {
                        cellEndDate.setCellValue((Date) null);
                        cellEndDate.setCellStyle(cellStyle);
                    } else if (voucherListError.get(i).getEndDate().isBefore(voucherListError.get(i).getStartDate())) {
                        cellEndDate.setCellValue(voucherListError.get(i).getEndDate());
                        cellEndDate.setCellStyle(cellStyleDate);
                    } else {
                        cellEndDate.setCellValue(voucherListError.get(i).getEndDate());
                        cellEndDate.setCellStyle(cellSymbleDate);
                    }
                }

            }
            errorSheet.autoSizeColumn(0);
            errorSheet.autoSizeColumn(1);
            errorSheet.autoSizeColumn(2);
            errorSheet.autoSizeColumn(3);
            errorSheet.autoSizeColumn(4);
            errorSheet.autoSizeColumn(5);
            errorSheet.autoSizeColumn(6);
            errorSheet.autoSizeColumn(7);
            errorSheet.autoSizeColumn(8);
            errorSheet.autoSizeColumn(9);
            errorSheet.autoSizeColumn(10);
            try (FileOutputStream errorOutputStream = new FileOutputStream(existingExcelFilePath)) {
                errorWorkbook.write(errorOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                errorWorkbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] exportExcelFileError() throws IOException {
        FileInputStream inputStream = new FileInputStream("src/main/java/com/backend/file/error-file-voucher.xlsx");
        // Đọc dữ liệu từ InputStream vào mảng byte
        byte[] excelBytes;
        try {
            excelBytes = inputStream.readAllBytes();
        } finally {
            inputStream.close();
        }
        return excelBytes;
    }

    // exportListVoucher
    @Override
    public List<VoucherOrderResponse> searchExportListVoucher(VoucherOrderRequest voucherOrderRequest) {
        if (voucherOrderRequest.getName() != null) {
            String name = voucherOrderRequest.getName();
            name = name.replaceAll("\\\\", "\\\\\\");
            name = name.replaceAll("%", "\\\\\\%");
            name = name.replaceAll("_", "\\\\\\_");
            voucherOrderRequest.setName(name);
        }
        List<Object> objects = voucherOrderCustomRepository.searchExportListVoucher(
                voucherOrderRequest.getName(),
                voucherOrderRequest.getStatus(),
                voucherOrderRequest.getStartDate(),
                voucherOrderRequest.getEndDate()
        );

        List<VoucherOrderResponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            VoucherOrderResponse voucherOrderResponse = convertPage(result);
            list.add(voucherOrderResponse);
        }
        return list;
    }

    @Override
    public byte[] exportExcelListVoucher(VoucherOrderRequest voucherOrderRequest) throws IOException {
        String excelResourcePath = "static/xuatExcel/danhSachVoucher.xlsx";
        String status;
        Resource resource = new ClassPathResource(excelResourcePath);
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        Sheet sheet = workbook.getSheet("Sheet1");

        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        // Đặt căn giữa ngang
        //        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Đặt căn giữa dọc
        //        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        // formatDate
        CellStyle dateCellStyle = workbook.createCellStyle();
        DataFormat dateFormat = workbook.createDataFormat();
        dateCellStyle.setDataFormat(dateFormat.getFormat("dd/MM/yyyy"));

        // set style căn giữa
        CellStyle centerAlignmentStyle = workbook.createCellStyle();
        centerAlignmentStyle.setWrapText(true);
        centerAlignmentStyle.setFont(font);
        centerAlignmentStyle.setAlignment(HorizontalAlignment.CENTER);
        centerAlignmentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAlignmentStyle.setBorderBottom(BorderStyle.THIN);
        centerAlignmentStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        centerAlignmentStyle.setBorderLeft(BorderStyle.THIN);
        centerAlignmentStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        centerAlignmentStyle.setBorderRight(BorderStyle.THIN);
        centerAlignmentStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        centerAlignmentStyle.setBorderTop(BorderStyle.THIN);
        centerAlignmentStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //

        List<VoucherOrderResponse> voucherOrderResponseList = this.searchExportListVoucher(voucherOrderRequest);
        int rowNum = 3;
        for (VoucherOrderResponse voucherOrderResponse : voucherOrderResponseList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 3);
            row.createCell(1).setCellValue(voucherOrderResponse.getCode());
            row.createCell(2).setCellValue(voucherOrderResponse.getName());
            row.createCell(3).setCellValue(voucherOrderResponse.getQuantity());
            BigDecimal giaTriGiam = voucherOrderResponse.getDiscountAmount();
            if (voucherOrderResponse.getReduceForm() == 0) {
                row.createCell(4).setCellValue(giaTriGiam.toString() + " VND");
            } else {
                row.createCell(4).setCellValue(giaTriGiam.toString() + " %");
            }
            BigDecimal giaTriDonHangToiThieu = voucherOrderResponse.getMinBillValue();
            row.createCell(5).setCellValue(giaTriDonHangToiThieu.toString() + " VND");
            if (voucherOrderResponse.getMaximumReductionValue() != null) {
                BigDecimal giaTriGiamToiDa = voucherOrderResponse.getMaximumReductionValue();
                row.createCell(6).setCellValue(giaTriGiamToiDa.toString() + " VND");
            } else {
                row.createCell(6).setCellValue("");
            }
            LocalDateTime localDateTime = voucherOrderResponse.getStartDate(); // Lấy giá trị từ cơ sở dữ liệu
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            //startDate
            Cell startDateCell = row.createCell(7);
            String startDate = localDateTime.format(formatter);
            startDateCell.setCellValue(startDate);

            //end Date
            Cell endDateCell = row.createCell(8);
            String endDate = localDateTime.format(formatter);
            endDateCell.setCellValue(endDate);


            if (voucherOrderResponse.getStatus() == 0) {
                status = "Chờ kích hoạt";
            } else if (voucherOrderResponse.getStatus() == 1) {
                status = "Đã kích hoạt";
            } else if (voucherOrderResponse.getStatus() == 2) {
                status = "Hết hạn";
            } else {
                status = "";
            }
            row.createCell(9).setCellValue(status);
//
//            Cell cell7 = row.createCell(7);
//            cell7.setCellValue(subjectDTO.getDescription());
//            cell7.setCellStyle(centerAlignmentStyle); // Áp dụng căn giữa cho cell7

            for (int i = 0; i <= 9; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                cell.setCellStyle(centerAlignmentStyle);
            }
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    @Override
    public CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Times New Roman");
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true); // Đặt đậm
        headerFont.setColor(IndexedColors.RED.getIndex()); // Đặt màu chữ thành đỏ
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Đặt căn giữa ngang
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Bỏ bottom align
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        return headerCellStyle;
    }

    @Override
    public CellStyle createErrorCellStyle(Workbook workbook) {
        CellStyle errorCellStyle = workbook.createCellStyle();
        Font fontError = workbook.createFont();
        fontError.setColor(IndexedColors.RED.getIndex()); // Đặt màu chữ thành đỏ
        fontError.setFontName("Times New Roman");
        fontError.setBold(true); // Đặt đậm
        fontError.setFontHeightInPoints((short) 12); // Đặt cỡ chữ là 12
        errorCellStyle.setWrapText(true);
        errorCellStyle.setBorderBottom(BorderStyle.THIN);
        errorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        errorCellStyle.setBorderLeft(BorderStyle.THIN);
        errorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        errorCellStyle.setBorderRight(BorderStyle.THIN);
        errorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        errorCellStyle.setBorderTop(BorderStyle.THIN);
        errorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        errorCellStyle.setFont(fontError);
        return errorCellStyle;
    }

    @Override
    public CellStyle createErrorCellStyleSymble(Workbook workbook) {
        CellStyle cellSymble = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman"); // Đặt font thành Times New Roman
        font.setFontHeightInPoints((short) 12); // Đặt cỡ chữ là 12
        cellSymble.setFont(font);
        cellSymble.setBorderBottom(BorderStyle.THIN);
        cellSymble.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellSymble.setBorderLeft(BorderStyle.THIN);
        cellSymble.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellSymble.setBorderRight(BorderStyle.THIN);
        cellSymble.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellSymble.setBorderTop(BorderStyle.THIN);
        cellSymble.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return cellSymble;
    }

    public CellStyle createErrorCellStyleDateSymble(Workbook workbook) {
        CellStyle cellSymbleDate = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellSymbleDate.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
        Font font = workbook.createFont();
        font.setFontName("Times New Roman"); // Đặt font thành Times New Roman
        font.setFontHeightInPoints((short) 12); // Đặt cỡ chữ là 12
        cellSymbleDate.setFont(font);
        cellSymbleDate.setBorderBottom(BorderStyle.THIN);
        cellSymbleDate.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellSymbleDate.setBorderLeft(BorderStyle.THIN);
        cellSymbleDate.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellSymbleDate.setBorderRight(BorderStyle.THIN);
        cellSymbleDate.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellSymbleDate.setBorderTop(BorderStyle.THIN);
        cellSymbleDate.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return cellSymbleDate;
    }

    @Override
    public CellStyle createBorderCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setTopBorderColor(IndexedColors.RED.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.RED.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.RED.getIndex());
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public CellStyle createBorderAndFormatDateCellStyle(Workbook workbook) {
        // Đặt định dạng ngày tháng
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));
        dateCellStyle.setBorderTop(BorderStyle.MEDIUM);
        dateCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        dateCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        dateCellStyle.setBorderRight(BorderStyle.MEDIUM);
        dateCellStyle.setTopBorderColor(IndexedColors.RED.getIndex());
        dateCellStyle.setBottomBorderColor(IndexedColors.RED.getIndex());
        dateCellStyle.setLeftBorderColor(IndexedColors.RED.getIndex());
        dateCellStyle.setRightBorderColor(IndexedColors.RED.getIndex());
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        dateCellStyle.setFont(font);
        return dateCellStyle;
    }
}