package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.orderCustomer.OrderCutomerRequest;
import com.backend.dto.request.orderCustomer.SearchOrderCutomerRequest;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Account;
import com.backend.entity.Order;
import com.backend.entity.OrderHistory;
import com.backend.entity.VoucherOrder;
import com.backend.repository.AccountRepository;
import com.backend.repository.OrderCustomRepository;
import com.backend.repository.OrderHistoryRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IOrderService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderCustomRepository orderCustomRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VoucherOrderRepository voucherOrderRepository;


    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    public OrderReponse convertPage(Object[] object) {
        OrderReponse orderReponse = new OrderReponse();
        orderReponse.setId(((BigInteger) object[0]).longValue());
        orderReponse.setNameVoucher((String) object[1]);
        orderReponse.setCode((String) object[2]);
        orderReponse.setType((String) object[3]);
        orderReponse.setCustomerName((String) object[4]);
        orderReponse.setPhoneNumber((String) object[5]);
        orderReponse.setAddress((String) object[6]);
        orderReponse.setShipFee((BigDecimal) object[7]);
        orderReponse.setMoneyReduce((BigDecimal) object[8]);
        orderReponse.setTotalMoney((BigDecimal) object[9]);
        orderReponse.setCreatedDate((Date) object[10]);
        orderReponse.setPayDate((Date) object[11]);
        orderReponse.setShipDate((Date) object[12]);
        orderReponse.setDesiredDate((Date) object[13]);
        orderReponse.setReceiveDate((Date) object[14]);
        orderReponse.setCreatedBy((String) object[15]);
        orderReponse.setUpdatedBy((String) object[16]);
        orderReponse.setNote((String) object[17]);
        orderReponse.setStatus((Integer) object[18]);
        return orderReponse;
    }

    @Override
    public Page<OrderReponse> searchOrder(SearchOrderRequest searchOrderRequest) {
        Pageable pageable = PageRequest.of(searchOrderRequest.getPage(), searchOrderRequest.getSize());
        if (searchOrderRequest.getCustomer() != null) {
            String customer = searchOrderRequest.getCustomer();
            customer = customer.replaceAll("\\\\", "\\\\\\\\");
            customer = customer.replaceAll("%", "\\\\%");
            customer = customer.replaceAll("_", "\\\\_");
            searchOrderRequest.setCustomer(customer);
        }
        Page<Object> objects = orderCustomRepository.doSearch(
                pageable,
                searchOrderRequest.getType(),
                searchOrderRequest.getVoucher(),
                searchOrderRequest.getCustomer(),
                searchOrderRequest.getDateFirst(),
                searchOrderRequest.getDateLast(),
                searchOrderRequest.getStatus(),
                searchOrderRequest.getPriceMin(),
                searchOrderRequest.getPriceMax()
        );
        List<OrderReponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            OrderReponse orderReponse = convertPage(result);
            list.add(orderReponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }

    @Override
    public ServiceResultReponse<Order> getOne(String code) {
        Optional<Order> order = orderRepository.findOrderByCode(code);
        if (order.isPresent()) {
            Order orderGet = order.get();
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderGet, "Đã tìm thấy order");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã order không tồn tại");

        }
    }

    // gen mã tự động
    public String generateOrderCode() {
        List<Order> invoices = orderRepository.findAll();
        int nextInvoiceNumber = invoices.size() + 1;
        return "HD" + String.format("%05d", nextInvoiceNumber);
    }

    @Override
    public ServiceResultReponse<Order> add(OrderRequest orderRequest) {
        List<Object> objectList = orderRepository.listOrderByStatus(0);
        if (!objectList.isEmpty() && objectList.size() >= 10) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Chỉ được tạo tối đa 10 hóa đơn chờ! ");
        } else {
            try {
                Date date = new Date();
                Order order = new Order();
                order.setCode(generateOrderCode());
                order.setCreatedBy(orderRequest.getCreatedBy());
//            order.setUpdatedBy(order.getUpdatedBy());
                order.setCreatedDate(date);
                order.setStatus(0);
                Order orderAdd = orderRepository.save(order);
                //
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setOrder(orderAdd);
                orderHistory.setCreatedTime(date);
                orderHistory.setCreatedBy(orderRequest.getCreatedBy());
                orderHistory.setNote("Nhân viên tạo đơn cho khách");
                orderHistory.setType("Created");
                orderHistoryRepository.save(orderHistory);
                //
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAdd, "Tạo hóa đơn thành công");
            } catch (Exception e) {
                e.printStackTrace();
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo hoá đơn thất bại");
            }
        }
    }

    @Override
    @Transactional
    public ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate) {
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        Date date = new Date();
        if (order.isPresent()) {
            Order orderGet = order.get();
            orderGet.setId(orderGet.getId());
            orderGet.setCode(orderGet.getCode());
            // voucher
            if (orderRequetUpdate.getIdVoucher() != null) {
                VoucherOrder voucherOrder = voucherOrderRepository.findById(orderRequetUpdate.getIdVoucher()).get();
                orderGet.setVoucherOrder(voucherOrder);
            } else {
                orderGet.setVoucherOrder(null);
            }
            //
            if (orderRequetUpdate.getIdAccount() != null) {
                Account account = accountRepository.findById(orderRequetUpdate.getIdAccount()).get();
                orderGet.setAccount(account);
            } else {
                orderGet.setAccount(null);
            }
            //
            orderGet.setType(orderRequetUpdate.getType());
            orderGet.setId(orderGet.getId());
            orderGet.setCustomerName(orderRequetUpdate.getCustomerName());
            orderGet.setPhoneNumber(orderRequetUpdate.getPhoneNumber());
            orderGet.setCustomerName(orderRequetUpdate.getCustomerName());
            orderGet.setAddress(orderRequetUpdate.getAddress());
            orderGet.setShipFee(orderRequetUpdate.getShipFee());
            orderGet.setMoneyReduce(orderRequetUpdate.getMoneyReduce());
            orderGet.setTotalMoney(orderRequetUpdate.getTotalMoney());
            orderGet.setCreatedDate(orderGet.getCreatedDate());
            orderGet.setPayDate(orderRequetUpdate.getPayDate());
            orderGet.setShipDate(orderRequetUpdate.getShipDate());
            orderGet.setDesiredDate(orderRequetUpdate.getDesiredDate());
            orderGet.setReceiveDate(orderRequetUpdate.getReceiveDate());
            orderGet.setCreatedBy(orderGet.getCreatedBy());
            orderGet.setUpdatedBy(orderRequetUpdate.getUpdatedBy());
            orderGet.setNote(orderRequetUpdate.getNote());
            orderGet.setStatus(orderRequetUpdate.getStatus());
            Order orderUpdate = orderRepository.save(orderGet);
            //
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderUpdate);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
            orderHistory.setNote(orderRequetUpdate.getNote());
            orderHistory.setType("Updated");
            orderHistoryRepository.save(orderHistory);
            //
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderUpdate, "Cập nhật hóa đơn thành công");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã hóa đơn không tồn tại!");
        }
    }

    @Override
    public ServiceResultReponse<Order> delete(OrderRequetUpdate orderRequetUpdate) {
        Date date = new Date();
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        if (order.isPresent()) {
            Order orderGet = order.get();
            orderGet.setUpdatedBy(orderRequetUpdate.getUpdatedBy());
            orderGet.setNote(orderRequetUpdate.getNote());
            orderGet.setStatus(orderRequetUpdate.getStatus());
            Order orderUpdate = orderRepository.save(orderGet);
            //
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderUpdate);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
            orderHistory.setNote(orderRequetUpdate.getNote());
            orderHistory.setType("Canceled");
            orderHistoryRepository.save(orderHistory);
            //
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 0L, orderUpdate, "Hủy hóa đơn thành công");
        } else {
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 0L, null, "Mã không tồn tại");
        }
    }
    @Override
    public ServiceResultReponse<?> getOrderByStatus(Integer status) {
        List<Object> objectList = orderRepository.listOrderByStatus(status);
        List<OrderReponse> list = new ArrayList<>();
        for (Object object : objectList) {
            Object[] result = (Object[]) object;
            OrderReponse orderReponse = convertPage(result);
            list.add(orderReponse);
        }
        return new ServiceResultReponse<>(AppConstant.SUCCESS, Long.valueOf(list.size()), list, "Lấy danh sách hóa đơn chờ thành công!");
    }

    //customer
    @Override
    public List<Order> listAllByCustomer(SearchOrderCutomerRequest searchOrderCutomerRequest) {
        return orderRepository.listOrderCustomer(searchOrderCutomerRequest.getIdAccount());
    }
//    @Override
    ServiceResultReponse<Order> customerAddOrder(OrderCutomerRequest orderCutomerRequest){
        try {
            Date date = new Date();
            Order order = new Order();
            order.setCode(generateOrderCode());
            if (orderCutomerRequest.getIdVoucher() != null) {
                VoucherOrder voucherOrder = voucherOrderRepository.findById(orderCutomerRequest.getIdVoucher()).get();
                order.setVoucherOrder(voucherOrder);
            } else {
                order.setVoucherOrder(null);
            }
            //
            if (orderCutomerRequest.getIdAccount() != null) {
                Account account = accountRepository.findById(orderCutomerRequest.getIdAccount()).get();
                order.setAccount(account);
            } else {
                order.setAccount(null);
            }

//            order.setUpdatedBy(order.getUpdatedBy());
            order.setType("2");
            order.setPhoneNumber(orderCutomerRequest.getPhoneNumber());
            order.setCustomerName(orderCutomerRequest.getCustomerName());
            order.setAddress(orderCutomerRequest.getAddress());
            order.setShipFee(orderCutomerRequest.getShipFee());
            order.setMoneyReduce(orderCutomerRequest.getMoneyReduce());
            order.setTotalMoney(orderCutomerRequest.getTotalMoney());
            order.setCreatedDate(date);
            order.setPayDate(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Thêm 2 ngày vào ngày giao hàng
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            Date shipDate = calendar.getTime();
            order.setShipDate(shipDate);

            // Thêm 2 ngày nữa vào ngày mong muốn
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            Date desiredDate = calendar.getTime();
            order.setDesiredDate(desiredDate);

            order.setReceiveDate(null);
            order.setCreatedBy(order.getAccount().getName());
            order.setNote(orderCutomerRequest.getNote());
            order.setStatus(4);
            Order orderAddCustomer = orderRepository.save(order);
            // orderHistory
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderAddCustomer);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(order.getCreatedBy());
            orderHistory.setNote("Khách Hàng Đặt Hàng");
            orderHistory.setType("Created");
            orderHistoryRepository.save(orderHistory);
            // orderDetail


            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAddCustomer, "Tạo đơn hàng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo đơn hàng thất bại");
        }
    }

    @Override
    public List<OrderReponse> searchOrderExport(SearchOrderRequest searchOrderRequest) {
        if (searchOrderRequest.getCustomer() != null) {
            String customer = searchOrderRequest.getCustomer();
            customer = customer.replaceAll("\\\\", "\\\\\\\\");
            customer = customer.replaceAll("%", "\\\\%");
            customer = customer.replaceAll("_", "\\\\_");
            searchOrderRequest.setCustomer(customer);
        }
        List<Object> objects = orderCustomRepository.getListExport(
                searchOrderRequest.getType(),
                searchOrderRequest.getVoucher(),
                searchOrderRequest.getCustomer(),
                searchOrderRequest.getDateFirst(),
                searchOrderRequest.getDateLast(),
                searchOrderRequest.getStatus(),
                searchOrderRequest.getPriceMin(),
                searchOrderRequest.getPriceMax()
        );
        List<OrderReponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            OrderReponse orderReponse = convertPage(result);
            list.add(orderReponse);
        }
        return list;
    }

    @Override
    public byte[] exportExcelListOrder(SearchOrderRequest searchOrderRequest) throws IOException {
        String excelResourcePath = "static/xuatExcel/danhSachHoaDon.xlsx";
        String type;
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

        List<OrderReponse> orderReponsesList = this.searchOrderExport(searchOrderRequest);
        int rowNum = 3;
        for (OrderReponse orderReponse : orderReponsesList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 3);
            row.createCell(1).setCellValue(orderReponse.getCode());
            row.createCell(2).setCellValue(orderReponse.getCustomerName());
            row.createCell(3).setCellValue(orderReponse.getPhoneNumber());
            if(orderReponse.getTotalMoney() != null) {
                BigDecimal totalMoney = orderReponse.getTotalMoney();
                row.createCell(4).setCellValue(totalMoney.toString());
            }
            if(orderReponse.getType() != null) {
                if (orderReponse.getType().equals("1")) {
                    type = "Tại quầy";
                } else
                    type = "Online";
            }else{
                type = "";
            }
            row.createCell(5).setCellValue(type);
            row.createCell(6).setCellValue(orderReponse.getCreatedDate());
            if (orderReponse.getStatus()== 0) {
                status = "Hóa đơn chờ";
            } else if(orderReponse.getStatus() == 1) {
                status = "Chờ thanh toán";
            }else if(orderReponse.getStatus() == 2) {
                status = "Đã thanh toán";
            }else if(orderReponse.getStatus() == 3) {
                status = "Đã hủy";
            }else if(orderReponse.getStatus() == 4) {
                status = "Chờ xác nhận";
            }else if(orderReponse.getStatus() == 5) {
                status = "Chờ giao hàng";
            }else if(orderReponse.getStatus() == 6){
                status = "Đơn hàng thành công";
            }else{
                status = "";
            }
            row.createCell(7).setCellValue(status);
//
//            Cell cell7 = row.createCell(7);
//            cell7.setCellValue(subjectDTO.getDescription());
//            cell7.setCellStyle(centerAlignmentStyle); // Áp dụng căn giữa cho cell7

            for (int i = 0; i <= 3; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
                cell.setCellStyle(cellStyle);
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}
