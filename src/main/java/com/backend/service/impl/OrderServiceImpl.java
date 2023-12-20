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
import com.backend.entity.Address;
import com.backend.entity.Cart;
import com.backend.entity.CartDetail;
import com.backend.entity.EmailTemplate;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.OrderHistory;
import com.backend.entity.Role;
import com.backend.entity.ShoeDetail;
import com.backend.entity.VoucherOrder;
import com.backend.repository.AccountRepository;
import com.backend.repository.AddressRepository;
import com.backend.repository.CartDetailRepository;
import com.backend.repository.CartRepository;
import com.backend.repository.EmailRepository;
import com.backend.repository.OrderCustomRepository;
import com.backend.repository.OrderDetailRepository;
import com.backend.repository.OrderHistoryRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IEmailTemplateService;
import com.backend.service.IOrderService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private ShoeDetailRepository shoeDetailRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private IEmailTemplateService iEmailTemplateService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private AddressRepository addressRepository;

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
        if (!objectList.isEmpty() && objectList.size() >= 20) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Chỉ được tạo tối đa 20 hóa đơn chờ! ");
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

    //
    private static BigDecimal previousTotalTongTien = BigDecimal.ZERO;

    public static BigDecimal getPreviousTotalTongTien() {
        return previousTotalTongTien;
    }

    public static void setPreviousTotalTongTien(BigDecimal totalTongTien) {
        previousTotalTongTien = totalTongTien;
    }

    //
    @Override
    @Transactional
    public ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate) {
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        Date date = new Date();
        int checkStatus5 = 0;
        List<String> tenGiayListStatus5 = new ArrayList<>();
        if (order.isPresent()) {
            Order orderGet = order.get();
            //
            BigDecimal tongTien = BigDecimal.ZERO;
            BigDecimal totalTongTien = BigDecimal.ZERO;
            BigDecimal oldTotalTongTien = BigDecimal.ZERO;
            //
            int check = 0;
            List<String> tenGiayList = new ArrayList<>();
            List<OrderDetail> orderDetailList = orderDetailRepository.getAllOrderDetail(orderGet.getId());
            for (OrderDetail orderDetail : orderDetailList) {
                ShoeDetail shoeDetail = shoeDetailRepository.findById(orderDetail.getShoeDetail().getId()).get();
                if (orderGet.getStatus() == 0 && orderDetail.getQuantity() > shoeDetail.getQuantity()) {
                    check++;
                    tenGiayList.add(shoeDetail.getCode());
                }
            }
            if (check > 0) {
                String errorMessage = "Số lượng sản phẩm tồn của giày ";
                errorMessage += String.join(", ", tenGiayList);
                errorMessage += " không đủ vui lòng chọn lại số lượng !";
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, errorMessage);
            } else {
                orderGet.setId(orderGet.getId());
                orderGet.setCode(orderGet.getCode());
                // voucher
                if (orderRequetUpdate.getIdVoucher() != null) {
                    VoucherOrder voucherOrder = voucherOrderRepository.findById(orderRequetUpdate.getIdVoucher()).get();
                    orderGet.setVoucherOrder(voucherOrder);
                    voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                    voucherOrderRepository.save(voucherOrder);
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
                orderGet.setAddress(orderRequetUpdate.getAddress());
                orderGet.setShipFee(orderRequetUpdate.getShipFee());
                orderGet.setMoneyReduce(orderRequetUpdate.getMoneyReduce());
                //
//                if (tongTien.compareTo(limit) > 0) {
//                    OrderHistory orderHistory = new OrderHistory();
//                    orderHistory.setOrder(orderGet);
//                    orderHistory.setCreatedTime(date);
//                    orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
//                    orderHistory.setNote(orderRequetUpdate.getNote());
//                    orderHistory.setType("Updated");
//                    orderHistoryRepository.save(orderHistory);
//                }
                orderGet.setTotalMoney(orderRequetUpdate.getTotalMoney());
                orderGet.setCreatedDate(orderGet.getCreatedDate());
                orderGet.setPayDate(orderRequetUpdate.getPayDate());
                orderGet.setShipDate(orderRequetUpdate.getShipDate());
                orderGet.setDesiredDate(orderRequetUpdate.getDesiredDate());
                orderGet.setReceiveDate(orderRequetUpdate.getReceiveDate());
                orderGet.setCreatedBy(orderGet.getCreatedBy());
                orderGet.setUpdatedBy(orderRequetUpdate.getUpdatedBy());
                orderGet.setNote(orderRequetUpdate.getNote());
                //
                if (orderGet.getStatus() == 0) {
                    for (OrderDetail orderDetail : orderDetailList) {
                        ShoeDetail shoeDetail = shoeDetailRepository.findById(orderDetail.getShoeDetail().getId()).get();
                        Integer quantityNew = shoeDetail.getQuantity() - orderDetail.getQuantity();
                        shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
                        if (quantityNew == 0) {
                            ShoeDetail shoeDetail2 = shoeDetailRepository.findById(shoeDetail.getId()).get();
                            shoeDetail2.setStatus(0);
                            shoeDetail2.setQuantity(0);
                            shoeDetailRepository.save(shoeDetail2);
                        }
                    }
                }
                //
                orderGet.setStatus(orderRequetUpdate.getStatus());
//                Order orderUpdate = orderRepository.save(orderGet);
                if (orderGet.getStatus() == 5) {
                    for (OrderDetail orderDetail : orderDetailList) {
                        ShoeDetail shoeDetail = shoeDetailRepository.findById(orderDetail.getShoeDetail().getId()).get();
                        if (orderDetail.getQuantity() > shoeDetail.getQuantity()) {
                            checkStatus5++;
                            tenGiayListStatus5.add(shoeDetail.getCode());
                        } else {
                            Integer quantityNew = shoeDetail.getQuantity() - orderDetail.getQuantity();
                            shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
                            if (quantityNew == 0) {
                                ShoeDetail shoeDetail2 = shoeDetailRepository.findById(shoeDetail.getId()).get();
                                shoeDetail2.setStatus(0);
                                shoeDetail2.setQuantity(0);
                                shoeDetailRepository.save(shoeDetail2);
                            }
                        }
                    }
                }
                if (orderGet.getStatus() != 5 && orderGet.getStatus() != 0) {
                    BigDecimal tempTotalTongTien = BigDecimal.ZERO;

                    for (OrderDetail orderDetail : orderDetailList) {
                        tongTien = orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                        tempTotalTongTien = tempTotalTongTien.add(tongTien);
                    }

                    BigDecimal previousTotalTongTien = this.getPreviousTotalTongTien();
                    if (!previousTotalTongTien.equals(tempTotalTongTien)) {
                        OrderHistory orderHistory = new OrderHistory();
                        orderHistory.setOrder(orderGet);
                        orderHistory.setCreatedTime(date);
                        orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
                        orderHistory.setNote(orderRequetUpdate.getNote());
                        orderHistory.setType("Updated");
                        orderHistoryRepository.save(orderHistory);
                    }

                    this.setPreviousTotalTongTien(tempTotalTongTien);
                }

            }
            if (checkStatus5 > 0) {
                orderGet.setStatus(4);
                orderRepository.save(orderGet);
                String errorMessage = "Số lượng sản phẩm tồn của giày ";
                errorMessage += String.join(", ", tenGiayList);
                errorMessage += " không đủ vui lòng chọn lại số lượng !";
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, errorMessage);
            } else {
                Order orderUpdate = orderRepository.save(orderGet);
                // kiểm tra xem có địa chỉ chưa, nếu chưa tạo địa chỉ mặc định cho khách hàng
                if (orderUpdate.getAccount() != null) {
                    List<Address> listCheckAddress = addressRepository.findAddressesByAccount_Id(orderUpdate.getAccount().getId());
                    if (listCheckAddress.isEmpty()) {
                        Address address = new Address();
                        address.setAccount(orderUpdate.getAccount());
                        address.setName(orderRequetUpdate.getCustomerName());
                        address.setPhoneNumber(orderRequetUpdate.getPhoneNumber());
                        address.setSpecificAddress(orderRequetUpdate.getSpecificAddress());
                        address.setWard(orderRequetUpdate.getWard());
                        address.setDistrict(orderRequetUpdate.getDistrict());
                        address.setProvince(orderRequetUpdate.getProvince());
                        address.setDefaultAddress("1");
                        addressRepository.save(address);
                    }
                }
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderUpdate, "Cập nhật hóa đơn thành công");
            }
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã hóa đơn không tồn tại!");
        }
    }


    //
    @Override
    @Transactional
    public ServiceResultReponse<Order> updateInformation(OrderRequetUpdate orderRequetUpdate) {
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
                voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                voucherOrderRepository.save(voucherOrder);
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
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderGet);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
            orderHistory.setNote(orderRequetUpdate.getNote());
            orderHistory.setType("Updated");
            orderHistoryRepository.save(orderHistory);
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderUpdate, "Cập nhật hóa đơn thành công");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã hóa đơn không tồn tại!");
        }
    }

    @Override
    @Transactional
    public ServiceResultReponse<Order> updateTien(OrderRequetUpdate orderRequetUpdate) {
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
                voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                voucherOrderRepository.save(voucherOrder);
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
            // Cập nhật lại số lượng giày
//            List<OrderDetail> orderDetailList = orderDetailRepository.getAllOrderDetail(orderGet.getId());
//            for (OrderDetail orderDetail : orderDetailList) {
//                ShoeDetail shoeDetail = shoeDetailRepository.findById(orderDetail.getShoeDetail().getId()).get();
//                Integer quantityNew = shoeDetail.getQuantity() + orderDetail.getQuantity();
//                shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
//            }
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    //customer
    @Override
    public List<Order> listAllByCustomer(SearchOrderCutomerRequest searchOrderCutomerRequest) {
        return orderRepository.listOrderCustomer(searchOrderCutomerRequest.getIdAccount());
    }

    @Override
    public ServiceResultReponse<Order> customerAddOrder(OrderCutomerRequest orderCutomerRequest) {
        try {
            Date date = new Date();
            Order order = new Order();
            order.setCode(generateOrderCode());
            if (orderCutomerRequest.getIdVoucher() != null) {
                VoucherOrder voucherOrder = voucherOrderRepository.findById(orderCutomerRequest.getIdVoucher()).get();
                order.setVoucherOrder(voucherOrder);
                voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                voucherOrderRepository.save(voucherOrder);
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
            // check xem có sản phẩm mua không!
            Cart cart = cartRepository.findByAccount_Id(order.getAccount().getId());
            List<CartDetail> cartDetailList = cartDetailRepository.listCartDetailByStatus(cart.getId());
            if (cartDetailList.isEmpty()) {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không đủ số lượng!");
            } else {
                int check = 0;
                List<String> tenGiayList = new ArrayList<>();
                for (CartDetail cartDetail : cartDetailList) {
                    ShoeDetail shoeDetail = shoeDetailRepository.findById(cartDetail.getShoeDetail().getId()).get();
                    if (cartDetail.getQuantity() > shoeDetail.getQuantity()) {
                        check++;
                        tenGiayList.add(shoeDetail.getCode());
                    }
                }
                if (check > 0) {
                    String errorMessage = "Số lượng sản phẩm tồn của giày ";
                    errorMessage += String.join(", ", tenGiayList);
                    errorMessage += " không đủ vui lòng chọn lại số lượng !";
                    return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, errorMessage);
                } else {
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
                    order.setShipDate(null);
                    order.setDesiredDate(null);
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
                    for (CartDetail cartDetail : cartDetailList) {
                        ShoeDetail shoeDetail = shoeDetailRepository.findById(cartDetail.getShoeDetail().getId()).get();
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setShoeDetail(shoeDetail);
                        orderDetail.setOrder(order);
                        orderDetail.setQuantity(cartDetail.getQuantity());
                        orderDetail.setPrice(shoeDetail.getPriceInput());
                        orderDetail.setDiscount(BigDecimal.valueOf(0));
                        orderDetail.setStatus(1);
//                        Integer quantityNew = shoeDetail.getQuantity() - orderDetail.getQuantity();
                        orderDetailRepository.save(orderDetail);
//                        shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
                        cartDetailRepository.deleteCartDetailByStatus(cartDetail.getStatus());
//                        if (quantityNew == 0) {
//                            ShoeDetail shoeDetail2 = shoeDetailRepository.findById(shoeDetail.getId()).get();
//                            shoeDetail2.setStatus(0);
//                            shoeDetail2.setQuantity(0);
//                            shoeDetailRepository.save(shoeDetail2);
//                        }
                    }
                    // gửi mail
                    Optional<EmailTemplate> emailTemplateCheckCustomer = emailRepository.checkSendMail(5);
                    if (emailTemplateCheckCustomer.isPresent()) {
                        EmailTemplate emailTemplate = emailTemplateCheckCustomer.get();
                        String to = order.getAccount().getEmail();
                        String subject = emailTemplate.getSubject();
                        String mailType = "";
                        String mailContent = emailTemplate.getMailContent();
                        iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                    } else {
                        String to = order.getAccount().getEmail();
                        String subject = "Xin chào bạn đến với store 2003SHOES";
                        String mailType = "";
                        String mailContent = "Cảm ơn bạn đã mua hàng. Nhớ đánh giá 5 sao cho store với nha!. Mãi yêuu!!!!!!!! ";
                        iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                    }
                    // kiểm tra xem có địa chỉ chưa, nếu chưa tạo địa chỉ mặc định cho khách hàng
                    if (orderCutomerRequest.getIdAccount() != null) {
                        List<Address> list = addressRepository.findAddressesByAccount_Id(orderCutomerRequest.getIdAccount());
                        if (list.isEmpty()) {
                            Address address = new Address();
                            address.setAccount(order.getAccount());
                            address.setName(orderCutomerRequest.getCustomerName());
                            address.setPhoneNumber(orderCutomerRequest.getPhoneNumber());
                            address.setSpecificAddress(orderCutomerRequest.getSpecificAddress());
                            address.setWard(orderCutomerRequest.getWard());
                            address.setDistrict(orderCutomerRequest.getDistrict());
                            address.setProvince(orderCutomerRequest.getProvince());
                            address.setDefaultAddress("1");
                            addressRepository.save(address);
                        }
                    }
                    return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAddCustomer, "Tạo đơn hàng thành công");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo đơn hàng thất bại");
        }
    }

    @Override
    public ServiceResultReponse<Order> customerNoLoginAddOrder(OrderCutomerRequest orderCutomerRequest) {
        try {
            if (orderCutomerRequest.getShoeDetailListRequets().isEmpty()) {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo đơn hàng thất bại, vui lòng chọn sản phẩm mua!");
            } else {
                List<ShoeDetail> shoeDetailList = new ArrayList<>();
                shoeDetailList = orderCutomerRequest.getShoeDetailListRequets();
                int check = 0;
                List<String> tenGiayList = new ArrayList<>();
                for (ShoeDetail shoeDetailCheck : shoeDetailList) {
                    ShoeDetail shoeDetailCheck1 = shoeDetailRepository.findById(shoeDetailCheck.getId()).get();
                    if (shoeDetailCheck.getQuantity() > shoeDetailCheck1.getQuantity()) {
                        check++;
                        tenGiayList.add(shoeDetailCheck1.getCode());
                    }
                }
                if (check > 0) {
                    String errorMessage = "Số lượng sản phẩm tồn của giày ";
                    errorMessage += String.join(", ", tenGiayList);
                    errorMessage += " không đủ vui lòng chọn lại số lượng !";
                    return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, errorMessage);
                } else {
                    Date date = new Date();
                    Order order = new Order();
                    order.setCode(generateOrderCode());
                    if (orderCutomerRequest.getIdVoucher() != null) {
                        VoucherOrder voucherOrder = voucherOrderRepository.findById(orderCutomerRequest.getIdVoucher()).get();
                        order.setVoucherOrder(voucherOrder);
                        voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                        voucherOrderRepository.save(voucherOrder);
                    } else {
                        order.setVoucherOrder(null);
                    }
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
                    order.setDesiredDate(null);
                    order.setReceiveDate(null);
                    order.setCreatedBy("Khách không đăng nhập");
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
//            Cart cart = cartRepository.findByAccount_Id(order.getAccount().getId());
//            List<CartDetail> cartDetailList = cartDetailRepository.listCartDetailByStatus(cart.getId());
                    for (ShoeDetail shoeDetail : shoeDetailList) {
                        OrderDetail orderDetail = new OrderDetail();
                        ShoeDetail shoeDetail1 = shoeDetailRepository.findById(shoeDetail.getId()).get();
                        orderDetail.setShoeDetail(shoeDetail1);
                        orderDetail.setOrder(order);
                        orderDetail.setQuantity(shoeDetail.getQuantity());
                        orderDetail.setPrice(shoeDetail.getPriceInput());
                        orderDetail.setDiscount(BigDecimal.valueOf(0));
                        orderDetail.setStatus(1);
//                        Integer quantityNew = shoeDetail1.getQuantity() - orderDetail.getQuantity();
                        orderDetailRepository.save(orderDetail);
//                        shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
//                        if (quantityNew == 0) {
//                            ShoeDetail shoeDetail2 = shoeDetailRepository.findById(shoeDetail.getId()).get();
//                            shoeDetail2.setStatus(0);
//                            shoeDetail2.setQuantity(0);
//                            shoeDetailRepository.save(shoeDetail2);
//                        }
                    }
                    //check xem email đã có tài khoản hay chưa
                    Optional<Account> accountCheck = accountRepository.getOneByEmail(orderCutomerRequest.getEmail());
                    if (accountCheck.isPresent()) {
                        Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(4);
                        if (emailTemplateCheck.isPresent()) {
                            EmailTemplate emailTemplate = emailTemplateCheck.get();
                            String to = orderCutomerRequest.getEmail();
                            String subject = emailTemplate.getSubject();
                            String mailType = "";
                            String mailContent = emailTemplate.getMailContent();
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        } else {
                            String to = orderCutomerRequest.getEmail();
                            String subject = "Xin chào bạn đến với store 2003SHOES";
                            String mailType = "Cảm ơn bạn đã mua hàng, bạn có thể xem lịch sử đơn hàng qua tài khoản đã được đăng ký với email này!";
                            String mailContent = "Nhớ đánh giá 5 sao cho store với nha!. Mãi yêuu";
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        }
                        // lưu đơn hàng vào tài khoản đã có
                        Account account = accountCheck.get();
                        Order orderAccount = orderRepository.findById(order.getId()).get();
                        orderAccount.setAccount(account);
                        orderRepository.save(orderAccount);
                        // check địa chỉ
                        List<Address> list = addressRepository.findAddressesByAccount_Id(account.getId());
                        if (list.isEmpty()) {
                            Address address = new Address();
                            address.setAccount(order.getAccount());
                            address.setName(orderCutomerRequest.getCustomerName());
                            address.setPhoneNumber(orderCutomerRequest.getPhoneNumber());
                            address.setSpecificAddress(orderCutomerRequest.getSpecificAddress());
                            address.setWard(orderCutomerRequest.getWard());
                            address.setDistrict(orderCutomerRequest.getDistrict());
                            address.setProvince(orderCutomerRequest.getProvince());
                            address.setDefaultAddress("1");
                            addressRepository.save(address);
                        }
                    } else {
                        // tạo tài khoản
                        Account account = new Account();
                        Calendar calendar1 = Calendar.getInstance();
                        Date now = calendar1.getTime();
                        account.setName(orderCutomerRequest.getCustomerName());
                        account.setEmail(orderCutomerRequest.getEmail());
                        account.setCreatedAt(now);
                        account.setUpdatedAt(now);
                        account.setStatus(1);
                        account.setPassword(passwordEncoder.encode("123456"));
                        account.setRole(Role.builder().id(2).build());
                        account = accountRepository.save(account);

                        // tạo cart
                        Cart cart = new Cart();
                        cart.setAccount(account);
                        cart.setCreatedAt(now);
                        cart.setUpdatedAt(now);
                        cart.setStatus(1);
                        cartRepository.save(cart);
                        //
                        Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(3);
                        if (emailTemplateCheck.isPresent()) {
                            EmailTemplate emailTemplate = emailTemplateCheck.get();
                            String to = orderCutomerRequest.getEmail();
                            String subject = emailTemplate.getSubject();
                            String mailType = "";
                            String mailContent = emailTemplate.getMailContent() + "123456";
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        } else {
                            String to = orderCutomerRequest.getEmail();
                            String subject = "Xin chào bạn đến với store 2003SHOES";
                            String mailType = "Cảm ơn bạn đã mua hàng, bạn có thể xem lịch sử đơn hàng qua tài khoản dưới đây";
                            String mailContent = "Mật khẩu tài khoản của bạn là : 123456";
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        }
                        // lưu đơn hàng vào tài khoản vừa tạo
                        Order orderAccount = orderRepository.findById(order.getId()).get();
                        orderAccount.setAccount(account);
                        orderRepository.save(orderAccount);
                        // lưu địa chỉ vào tài khoản vừa tạo
                        List<Address> list = addressRepository.findAddressesByAccount_Id(account.getId());
                        if (list.isEmpty()) {
                            Address address = new Address();
                            address.setAccount(order.getAccount());
                            address.setName(orderCutomerRequest.getCustomerName());
                            address.setPhoneNumber(orderCutomerRequest.getPhoneNumber());
                            address.setSpecificAddress(orderCutomerRequest.getSpecificAddress());
                            address.setWard(orderCutomerRequest.getWard());
                            address.setDistrict(orderCutomerRequest.getDistrict());
                            address.setProvince(orderCutomerRequest.getProvince());
                            address.setDefaultAddress("1");
                            addressRepository.save(address);
                        }
                    }
                    return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAddCustomer, "Tạo đơn hàng thành công");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo đơn hàng thất bại");
        }
    }

    @Override
    public ServiceResultReponse<Order> customerByNow(OrderCutomerRequest orderCutomerRequest) {
        try {
            List<ShoeDetail> shoeDetailList = new ArrayList<>();
            shoeDetailList = orderCutomerRequest.getShoeDetailListRequets();
            boolean check = false;
            String tenGiay = "";
            for (ShoeDetail shoeDetailCheck : shoeDetailList) {
                ShoeDetail shoeDetailCheck1 = shoeDetailRepository.findById(shoeDetailCheck.getId()).get();
                if (shoeDetailCheck.getQuantity() > shoeDetailCheck1.getQuantity()) {
                    check = true;
                    tenGiay = shoeDetailCheck1.getCode();
                }
            }
            if (check == true) {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Số lượng sản phẩm tồn của giày " + tenGiay + " không đủ vui lòng chọn lại số lượng");
            } else {
                Date date = new Date();
                Order order = new Order();
                order.setCode(generateOrderCode());
                if (orderCutomerRequest.getIdVoucher() != null) {
                    VoucherOrder voucherOrder = voucherOrderRepository.findById(orderCutomerRequest.getIdVoucher()).get();
                    order.setVoucherOrder(voucherOrder);
                    voucherOrder.setQuantity(voucherOrder.getQuantity() - 1);
                    voucherOrderRepository.save(voucherOrder);
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

                order.setShipDate(null);
                order.setDesiredDate(null);
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
                for (ShoeDetail shoeDetail : shoeDetailList) {
                    OrderDetail orderDetail = new OrderDetail();
                    ShoeDetail shoeDetail1 = shoeDetailRepository.findById(shoeDetail.getId()).get();
                    orderDetail.setShoeDetail(shoeDetail1);
                    orderDetail.setOrder(order);
                    orderDetail.setQuantity(shoeDetail.getQuantity());
                    orderDetail.setPrice(shoeDetail.getPriceInput());
                    orderDetail.setDiscount(BigDecimal.valueOf(0));
                    orderDetail.setStatus(1);
//                    Integer quantityNew = shoeDetail1.getQuantity() - orderDetail.getQuantity();
                    orderDetailRepository.save(orderDetail);
//                    shoeDetailRepository.updateSoLuong(quantityNew, shoeDetail.getId());
//                    if (quantityNew == 0) {
//                        ShoeDetail shoeDetail2 = shoeDetailRepository.findById(shoeDetail.getId()).get();
//                        shoeDetail2.setStatus(0);
//                        shoeDetail2.setQuantity(0);
//                        shoeDetailRepository.save(shoeDetail2);
//                    }
                }
                Optional<EmailTemplate> emailTemplateCheckCustomer = emailRepository.checkSendMail(5);
                if (emailTemplateCheckCustomer.isPresent()) {
                    EmailTemplate emailTemplate = emailTemplateCheckCustomer.get();
                    String to = order.getAccount().getEmail();
                    String subject = emailTemplate.getSubject();
                    String mailType = "";
                    String mailContent = emailTemplate.getMailContent();
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                } else {
                    String to = order.getAccount().getEmail();
                    String subject = "Xin chào bạn đến với store 2003SHOES";
                    String mailType = "";
                    String mailContent = "Cảm ơn bạn đã mua hàng. Nhớ đánh giá 5 sao cho store với nha!. Mãi yêuu!!!!!!!! ";
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                }
                // kiểm tra xem có địa chỉ chưa, nếu chưa tạo địa chỉ mặc định cho khách hàng
                if (orderCutomerRequest.getIdAccount() != null) {
                    List<Address> list = addressRepository.findAddressesByAccount_Id(orderCutomerRequest.getIdAccount());
                    if (list.isEmpty()) {
                        Address address = new Address();
                        address.setAccount(order.getAccount());
                        address.setName(orderCutomerRequest.getCustomerName());
                        address.setPhoneNumber(orderCutomerRequest.getPhoneNumber());
                        address.setSpecificAddress(orderCutomerRequest.getSpecificAddress());
                        address.setWard(orderCutomerRequest.getWard());
                        address.setDistrict(orderCutomerRequest.getDistrict());
                        address.setProvince(orderCutomerRequest.getProvince());
                        address.setDefaultAddress("1");
                        addressRepository.save(address);
                    }
                }
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAddCustomer, "Tạo đơn hàng thành công");
            }
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
        CellStyle cellSymbleDate = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellSymbleDate.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));

        List<OrderReponse> orderReponsesList = this.searchOrderExport(searchOrderRequest);
        int rowNum = 3;
        for (OrderReponse orderReponse : orderReponsesList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 3);
            row.createCell(1).setCellValue(orderReponse.getCode());
            row.createCell(2).setCellValue(orderReponse.getCustomerName());
            row.createCell(3).setCellValue(orderReponse.getPhoneNumber());
            if (orderReponse.getTotalMoney() != null) {
                BigDecimal totalMoney = orderReponse.getTotalMoney();
                row.createCell(4).setCellValue(totalMoney.toString() + " VND");
            }
            if (orderReponse.getType() != null) {
                if (orderReponse.getType().equals("1")) {
                    type = "Tại quầy";
                } else
                    type = "Online";
            } else {
                type = "";
            }
            row.createCell(5).setCellValue(type);

            Cell createDate = row.createCell(6);
            createDate.setCellValue(orderReponse.getCreatedDate());
//            createDate.setCellStyle(cellSymbleDate);
            createDate.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(orderReponse.getCreatedDate()));

            if (orderReponse.getStatus() == 0) {
                status = "Hóa đơn chờ";
            } else if (orderReponse.getStatus() == 1) {
                status = "Chờ thanh toán";
            } else if (orderReponse.getStatus() == 2) {
                status = "Đã thanh toán";
            } else if (orderReponse.getStatus() == 3) {
                status = "Đã hủy";
            } else if (orderReponse.getStatus() == 4) {
                status = "Chờ xác nhận";
            } else if (orderReponse.getStatus() == 5) {
                status = "Đã xác nhận";
            } else if (orderReponse.getStatus() == 6) {
                status = "Chờ giao hàng";
            } else if (orderReponse.getStatus() == 7) {
                status = "Đã bàn giao";
            } else if (orderReponse.getStatus() == 8) {
                status = "Hoàn thành";
            } else {
                status = "";
            }
            row.createCell(7).setCellValue(status);

            for (int i = 0; i <= 7; i++) {
                Cell cell = row.getCell(i);
                if (cell == null) {
                    cell = row.createCell(i);
                }
//                cell.setCellValue(cell.getStringCellValue());
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }


}
