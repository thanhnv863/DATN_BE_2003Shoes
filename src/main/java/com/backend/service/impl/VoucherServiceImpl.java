package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.dto.response.shoedetail.Meta;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Order;
import com.backend.entity.ShoeDetail;
import com.backend.entity.VoucherOrder;
import com.backend.repository.VoucherOrderCustomRepository;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                voucherHoaDon.setName(voucherOrderRequest.getName());
                voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());
                voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                voucherHoaDon.setCreateDate(currentDateTime);
                voucherHoaDon.setUpdateAt(currentDateTime);
                voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());
                //voucherHoaDon.setStatus(0);

                // Kiểm tra xem người dùng đã cung cấp giá trị status chưa
                if (voucherOrderRequest.getStatus() != null) {
                    voucherHoaDon.setStatus(voucherOrderRequest.getStatus());
                } else {
                    // Nếu người dùng không cung cấp giá trị status, đặt mặc định là 0
                    voucherHoaDon.setStatus(0);
                }

                voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                //VoucherOrderResponse convertVoucherOrderResponse = convertPage(voucherHoaDon);
                return new ServiceResult<>(AppConstant.SUCCESS, "Add thành công", voucherHoaDon);
            } catch (Exception e) {
                // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
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
                    voucherHoaDon.setName(voucherOrderRequest.getName());
                    voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());
                    voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                    voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                    voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                    voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                    voucherHoaDon.setUpdateAt(currentDateTime);
                    voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());
                    // voucherHoaDon.setStatus(0);

                    //Kiểm tra xem người dùng đã cung cấp giá trị status chưa
                    if (voucherOrderRequest.getStatus() != null) {
                        voucherHoaDon.setStatus(voucherOrderRequest.getStatus());
                    } else {
                        // Nếu người dùng không cung cấp giá trị status, đặt mặc định là 0
                        voucherHoaDon.setStatus(0);
                    }

                    voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                    //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                    return new ServiceResult<>(AppConstant.SUCCESS, "Update thành công", voucherHoaDon);
                } catch (Exception e) {
                    // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                    //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
                }
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
            if (voucher.getStatus() != 3) { //status=3: khi xoá đổi trạng thái thành ẩn
                // Chỉ cập nhật status nếu status hiện tại không phải là 3
                if (currentDateTime.isAfter(voucher.getStartDate()) && currentDateTime.isBefore(voucher.getEndDate())) {
                    voucher.setStatus(1); // Cập nhật thành "đã kích hoạt"
                } else if (currentDateTime.isAfter(voucher.getEndDate())) {
                    voucher.setStatus(2); // Cập nhật thành "hết hạn"
                } else {
                    voucher.setStatus(0); // Cập nhật thành chờ kích hoạt
                }
                voucherOrderRepository.save(voucher);
            }
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

        Timestamp startDateTimestamp = (Timestamp) object[6];
        voucherOrderResponse.setStartDate(startDateTimestamp.toLocalDateTime());

        // Convert java.sql.Timestamp to java.time.LocalDateTime
        Timestamp endDateTimestamp = (Timestamp) object[7];
        voucherOrderResponse.setEndDate(endDateTimestamp.toLocalDateTime());

        // Convert java.sql.Timestamp to java.time.LocalDateTime
        Timestamp createDateTimestamp = (Timestamp) object[8];
        voucherOrderResponse.setCreateDate(createDateTimestamp.toLocalDateTime());

        // Convert java.sql.Timestamp to java.time.LocalDateTime
        Timestamp updateAtTimestamp = (Timestamp) object[9];
        voucherOrderResponse.setUpdateAt(updateAtTimestamp.toLocalDateTime());

        voucherOrderResponse.setReduceForm((Integer) object[10]);
        voucherOrderResponse.setStatus((Integer) object[11]);
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
                voucherHoaDon.setUpdateAt(currentDateTime);
                voucherHoaDon.setStatus(3);
                voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                //VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                return new ServiceResult<>(AppConstant.SUCCESS, "Delete thành công", voucherHoaDon);
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
        if (voucherOrderRequest.getCode() == null) {
            errorMessages.add("Code không được để trống");
        }
        if (voucherOrderRequest.getName() == null) {
            errorMessages.add("Name không được để trống");
        }
        if (voucherOrderRequest.getName() != null && voucherOrderRequest.getName().length() > 255) {
            errorMessages.add("Name không được quá 255 ký tự");
        }
        if (voucherOrderRequest.getQuantity() == null) {
            errorMessages.add("Quantity không được để trống");
        }
        if (voucherOrderRequest.getDiscountAmount() == null) {
            errorMessages.add("Discount amount không được để trống");
        }
        if (voucherOrderRequest.getMinBillValue() == null) {
            errorMessages.add("Min bill value không được để trống");
        }
        if (voucherOrderRequest.getStartDate() == null) {
            errorMessages.add("Start date không được để trống");
        }
        if (voucherOrderRequest.getEndDate() == null) {
            errorMessages.add("End date không được để trống");
        }
        if (voucherOrderRequest.getReduceForm() == null) {
            errorMessages.add("Reduce form không được để trống");
        }

        // Kiểm tra số lượng
        if (voucherOrderRequest.getQuantity() != null && voucherOrderRequest.getQuantity() <= 0) {
            errorMessages.add("Quantity phải lớn hơn 0");
        }
        if (voucherOrderRequest.getQuantity() != null && (voucherOrderRequest.getQuantity() < 1 || voucherOrderRequest.getQuantity() > 100)) {
            errorMessages.add("Quantity phải nằm trong khoảng từ 1 đến 100");
        }
        if (voucherOrderRequest.getDiscountAmount() != null && voucherOrderRequest.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errorMessages.add("Discount amount phải lớn hơn 0");
        }
        if (voucherOrderRequest.getMinBillValue() != null && voucherOrderRequest.getMinBillValue().compareTo(BigDecimal.ZERO) <= 0) {
            errorMessages.add("Min bill value phải lớn hơn 0");
        }

        //Kiểm tra trạng thái và hình thức giảm
        if (voucherOrderRequest.getReduceForm() != null && (voucherOrderRequest.getReduceForm() < 0 || voucherOrderRequest.getReduceForm() > 1)) {
            errorMessages.add("Reduce form phải nằm trong khoảng 0 và 1");
        }
        if (voucherOrderRequest.getStatus() != null && (voucherOrderRequest.getStatus() < 0 || voucherOrderRequest.getStatus() > 2)) {
            errorMessages.add("Status phải nằm trong khoảng từ 0 đến 2");
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (voucherOrderRequest.getStartDate() != null && voucherOrderRequest.getEndDate() != null) {
            if (voucherOrderRequest.getEndDate().isBefore(voucherOrderRequest.getStartDate())) {
                errorMessages.add("End date phải sau Start date");
            }
        }
        if (voucherOrderRequest.getCreateDate() != null && voucherOrderRequest.getUpdateAt() != null) {
            if (voucherOrderRequest.getStartDate().isBefore(voucherOrderRequest.getCreateDate())) {
                errorMessages.add("Start date phải sau Create date");
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
                voucherOrderRequest.getStatus()
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

}