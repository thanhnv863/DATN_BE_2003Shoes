package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.dto.response.shoedetail.Meta;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.ShoeDetail;
import com.backend.entity.VoucherOrder;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.Tuple;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherServiceImpl implements IVoucherOrderService {

    @Autowired
    private VoucherOrderRepository voucherOrderRepository;

    public ServiceResult<VoucherOrderResponse> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL, mess, null);
    }

    public ServiceResult<VoucherOrderResponse> validateVoucher(VoucherOrderRequest voucherOrderRequest) {
        List<String> errorMessages = new ArrayList<>();

        //Validate trống
        if (voucherOrderRequest.getCode() == null) {
            return result("Code không được để trống");
        }
        if (voucherOrderRequest.getName() == null) {
            return result("Name không được để trống");
        }
        if (voucherOrderRequest.getQuantity() == null) {
            return result("Quantity không được để trống");
        }
        if (voucherOrderRequest.getDiscountAmount() == null) {
            return result("Discount amount không được để trống");
        }
        if (voucherOrderRequest.getMinBillValue() == null) {
            return result("Min bill value không được để trống");
        }
        if (voucherOrderRequest.getStartDate() == null) {
            return result("Start date không được để trống");
        }
        if (voucherOrderRequest.getEndDate() == null) {
            return result("End date không được để trống");
        }
        if (voucherOrderRequest.getReduceForm() == null) {
            return result("Reduce form không được để trống");
        }

        // Nếu không có lỗi, trả về một ServiceResult thành công với dữ liệu rỗng
        //return new ServiceResult<>(AppConstant.SUCCESS, "Dữ liệu hợp lệ", null);
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrderResponse> addVoucher(VoucherOrderRequest voucherOrderRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Tạo một UUID mới
        UUID uuid = UUID.randomUUID();

        // Lấy giá trị chuỗi của UUID
        String randomString = uuid.toString();

        // Cắt chuỗi UUID để sử dụng một phần cụ thể, ví dụ: 8 ký tự đầu
        String cutRandomString = randomString.substring(0, 8);

        VoucherOrder voucherHoaDon = new VoucherOrder();
        try {
            ServiceResult<VoucherOrderResponse> validationResult = validateVoucher(voucherOrderRequest);
            if (validationResult != null) {
                return validationResult;
            } else {
                voucherHoaDon.setCode("Code voucher " + cutRandomString);
                voucherHoaDon.setName(voucherOrderRequest.getName());
                voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());
                voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                voucherHoaDon.setCreateDate(currentDateTime);
                voucherHoaDon.setUpdateAt(currentDateTime);
                voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());
                voucherHoaDon.setStatus(0);
                voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
            }
        } catch (Exception e) {
            // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
            VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceResult<>(AppConstant.FAIL, "Add thất bại", convertVoucherOrderResponse); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
        }
        VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
        return new ServiceResult<>(AppConstant.SUCCESS, "Add thành công", convertVoucherOrderResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<VoucherOrderResponse> updateVoucher(VoucherOrderRequest voucherOrderRequest, Long id) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<VoucherOrder> optionalVoucherHoaDon = voucherOrderRepository.findById(id);
        if (optionalVoucherHoaDon.isPresent()) {
            VoucherOrder voucherHoaDon = optionalVoucherHoaDon.get();
            try {
                ServiceResult<VoucherOrderResponse> validationResult = validateVoucher(voucherOrderRequest);
                if (validationResult != null) {
                    return validationResult;
                } else {
                    voucherHoaDon.setName(voucherOrderRequest.getName());
                    voucherHoaDon.setQuantity(voucherOrderRequest.getQuantity());
                    voucherHoaDon.setDiscountAmount(voucherOrderRequest.getDiscountAmount());
                    voucherHoaDon.setMinBillValue(voucherOrderRequest.getMinBillValue());
                    voucherHoaDon.setStartDate(voucherOrderRequest.getStartDate());
                    voucherHoaDon.setEndDate(voucherOrderRequest.getEndDate());
                    voucherHoaDon.setUpdateAt(currentDateTime);
                    voucherHoaDon.setReduceForm(voucherOrderRequest.getReduceForm());
                    voucherHoaDon.setStatus(0);
                    voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
                }
            } catch (Exception e) {
                // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.FAIL, e.getMessage(), convertVoucherOrderResponse); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
            }
            VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
            return new ServiceResult<>(AppConstant.SUCCESS, "Update thành công", convertVoucherOrderResponse);
        } else {
            return new ServiceResult<>(AppConstant.FAIL, "Id không tồn tại", null);
        }
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void updateVoucherStatus() {
        List<VoucherOrder> vouchers = voucherOrderRepository.findAll();
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (VoucherOrder voucher : vouchers) {
            if (voucher.getStartDate() == null || voucher.getEndDate() == null) {
                // Bỏ qua voucher này nếu có startDate hoặc endDate là null
                continue;
            }
            if (voucher.getStatus() != 3) {
                // Chỉ cập nhật status nếu status hiện tại không phải là 4
                if (currentDateTime.isAfter(voucher.getStartDate()) && currentDateTime.isBefore(voucher.getEndDate())) {
                    voucher.setStatus(1); // Cập nhật thành "đã kích hoạt"
                } else if (currentDateTime.isAfter(voucher.getEndDate())) {
                    voucher.setStatus(2); // Cập nhật thành "hết hạn"
                } else {
                    voucher.setStatus(0);
                }
                voucherOrderRepository.save(voucher);
            }
        }
    }

    @Override
    public VoucherOrderResponse convertToResponse(VoucherOrder voucherOrder) {
        return VoucherOrderResponse.builder()
                .code(voucherOrder.getCode())
                .name(voucherOrder.getName())
                .quantity(voucherOrder.getQuantity())
                .discountAmount(voucherOrder.getDiscountAmount())
                .minBillValue(voucherOrder.getMinBillValue())
                .startDate(voucherOrder.getStartDate())
                .endDate(voucherOrder.getEndDate())
                .createDate(voucherOrder.getCreateDate())
                .updateAt(voucherOrder.getUpdateAt())
                .reduceForm(voucherOrder.getReduceForm())
                .status(voucherOrder.getStatus())
                .build();
    }

    @Override
    public ServiceResult<List<DataPaginate>> getAllVoucherOrder(int page, int size) {
        Page<VoucherOrder> voucherOrders = voucherOrderRepository.getAllVoucherOrder(PageRequest.of(page, size));

        int current = voucherOrders.getNumber();
        int pageSize = voucherOrders.getSize();
        int pages = voucherOrders.getTotalPages();
        long total = voucherOrders.getTotalElements();

        Meta meta = new Meta();
        meta.setCurrent(current);
        meta.setPageSize(pageSize);
        meta.setPages(pages);
        meta.setTotal(total);

        List<VoucherOrderResponse> voucherOrderResponses = new ArrayList<>();

        for (VoucherOrder voucherOrder : voucherOrders) {
            VoucherOrderResponse voucherOrderResponse = new VoucherOrderResponse();
            voucherOrderResponse.setId(voucherOrder.getId());
            voucherOrderResponse.setCode(voucherOrder.getCode());
            voucherOrderResponse.setName(voucherOrder.getName());
            voucherOrderResponse.setQuantity(voucherOrder.getQuantity());
            voucherOrderResponse.setDiscountAmount(voucherOrder.getDiscountAmount());
            voucherOrderResponse.setMinBillValue(voucherOrder.getMinBillValue());
            voucherOrderResponse.setStartDate(voucherOrder.getStartDate());
            voucherOrderResponse.setEndDate(voucherOrder.getEndDate());
            voucherOrderResponse.setCreateDate(voucherOrder.getCreateDate());
            voucherOrderResponse.setUpdateAt(voucherOrder.getUpdateAt());
            voucherOrderResponse.setReduceForm(voucherOrder.getReduceForm());
            voucherOrderResponse.setStatus(voucherOrder.getStatus());

            voucherOrderResponses.add(voucherOrderResponse);
        }

        DataPaginate dataPaginate = new DataPaginate();
        dataPaginate.setMeta(meta);
        dataPaginate.setVoucherOrderResponses(voucherOrderResponses);

        return new ServiceResult(AppConstant.SUCCESS,
                "Successfully retrieved",
                dataPaginate
        );
    }

    @Override
    public ServiceResult<VoucherOrderResponse> deleteVoucher(Long id) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Optional<VoucherOrder> optionalVoucherHoaDon = voucherOrderRepository.findById(id);
        if (optionalVoucherHoaDon.isPresent()) {
            VoucherOrder voucherHoaDon = optionalVoucherHoaDon.get();
            try {
                voucherHoaDon.setUpdateAt(currentDateTime);
                voucherHoaDon.setStatus(3);
                voucherHoaDon = voucherOrderRepository.save(voucherHoaDon);
            } catch (Exception e) {
                // Xảy ra lỗi, gọi rollback để hoàn tác các thay đổi
                VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.FAIL, e.getMessage(), convertVoucherOrderResponse); // hoặc xử lý lỗi một cách thích hợp dựa trên nhu cầu của bạn
            }
            VoucherOrderResponse convertVoucherOrderResponse = convertToResponse(voucherHoaDon);
            return new ServiceResult<>(AppConstant.SUCCESS, "Delete thành công", convertVoucherOrderResponse);
        } else {
            return new ServiceResult<>(AppConstant.FAIL, "Id không tồn tại", null);
        }
    }
}
