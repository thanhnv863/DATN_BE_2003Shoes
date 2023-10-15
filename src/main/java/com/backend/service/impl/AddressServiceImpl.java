package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.AddressRequest;
import com.backend.dto.response.AddressResponse;
import com.backend.entity.Account;
import com.backend.entity.Address;
import com.backend.repository.AccountRepository;
import com.backend.repository.AddressRepository;
import com.backend.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public ServiceResult<AddressResponse> addAddress(AddressRequest addressRequest) {

        Address address = new Address();

        String result = validateAddress(addressRequest);

        if (result != null){
            return result(result);
        }else {
           try{
               Optional<Account> account = accountRepository.findById(addressRequest.getAccountId());
               if (account.isPresent()){
                   Account account1 = account.get();
                   address.setAccount(account1);
                   address.setName(addressRequest.getName());
                   address.setPhoneNumber(addressRequest.getPhoneNumber());
                   address.setSpecificAddress(addressRequest.getSpecificAddress());
                   address.setWard(addressRequest.getWard());
                   address.setDistrict(addressRequest.getDistrict());
                   address.setProvince(addressRequest.getProvince());
                   address.setNote(addressRequest.getNote());
                   address.setDefaultAddress(addressRequest.getDefaultAddress());
               }else{
                  return new ServiceResult<>(AppConstant.FAIL,"Add fail",null);
               }

               address = addressRepository.save(address);
               AddressResponse convertAddressResponse = convertToResponse(address);

               return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertAddressResponse);
           }catch (Exception e){
               e.printStackTrace();
               return new ServiceResult<>(AppConstant.BAD_REQUEST,e.getMessage(),null);
           }
        }
    }

    @Override
    public ServiceResult<AddressResponse> updateAddress(AddressRequest addressRequest, Long id) {
        Optional<Address> addressId = addressRepository.findById(id);
        Optional<Account> accountId = accountRepository.findById(addressRequest.getAccountId());
        Account account = accountId.get();
        if (addressId.isPresent()){
            Address address = addressId.get();
            String result = validateAddress(addressRequest);
            if(result != null){
                return result(result);
            }else{
                try{
                    address.setAccount(account);
                    address.setName(addressRequest.getName());
                    address.setPhoneNumber(addressRequest.getPhoneNumber());
                    address.setSpecificAddress(addressRequest.getSpecificAddress());
                    address.setWard(addressRequest.getWard());
                    address.setDistrict(addressRequest.getDistrict());
                    address.setProvince(addressRequest.getProvince());
                    address.setNote(addressRequest.getNote());
                    address.setDefaultAddress(addressRequest.getDefaultAddress());

                    address = addressRepository.save(address);
                    AddressResponse convertAddressResponse = convertToResponse(address);
                    return new ServiceResult<>(AppConstant.SUCCESS,"Update success",convertAddressResponse);

                }catch (Exception e){
                    e.printStackTrace();
                    return new ServiceResult<>(AppConstant.BAD_REQUEST,e.getMessage(),null);
                }
            }
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"Id not exist",null);
        }
    }

    @Override
    public ServiceResult<List<AddressResponse>> getAllAddress() {
        List<Address> addressList = addressRepository.findAll();

        List<AddressResponse> addressResponses = new ArrayList<>();

        for (Address address: addressList){
            AddressResponse addressResponse = new AddressResponse();

            addressResponse.setId(address.getId());
            addressResponse.setAccountId(address.getAccount().getId());
            addressResponse.setName(address.getName());
            addressResponse.setPhoneNumber(address.getPhoneNumber());
            addressResponse.setSpecificAddress(address.getSpecificAddress());
            addressResponse.setWard(address.getWard());
            addressResponse.setDistrict(address.getDistrict());
            addressResponse.setProvince(address.getProvince());
            addressResponse.setNote(address.getNote());
            addressResponse.setDefaultAddress(address.getDefaultAddress());

            addressResponses.add(addressResponse);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,
                "Successfully retrieved",
                    addressResponses);
    }

    @Override
    public ServiceResult<AddressResponse> deleteAddress(Long id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isPresent()){
            Address address = optionalAddress.get();
            addressRepository.deleteById(address.getId());
            return new ServiceResult<>(AppConstant.SUCCESS,"delete Success",null);
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"Id not exist",null);
        }
    }

    @Override
    public String validateAddress(AddressRequest addressRequest) {

        List<String> errorMessages = new ArrayList<>();

        if (addressRequest.getAccountId() == null){
            errorMessages.add("id_account không được để trông");
        }
        if (addressRequest.getName() == null){
            errorMessages.add("tên không được để trống");
        }
        if (addressRequest.getPhoneNumber() == null){
            errorMessages.add("Không được để trống phone number");
        }
        if(addressRequest.getSpecificAddress() == null){
            errorMessages.add("Không được để trống địa chỉ cụ thể");
        }
        if(addressRequest.getWard() == null){
            errorMessages.add("không được để trống ward");
        }
        if(addressRequest.getDistrict() == null){
            errorMessages.add("Không được để trống quận");
        }
        if(addressRequest.getProvince() == null){
            errorMessages.add("Không được để trống tỉnh");
        }
        if(addressRequest.getDefaultAddress() == null){
            errorMessages.add("Không được để trống địa chỉ mặc định");
        }

        // kiểm tra định dạng
        String reg = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";

        boolean kt = addressRequest.getPhoneNumber().matches(reg);

        if (addressRequest.getPhoneNumber() != null && kt == false){
            errorMessages.add("Số điện thoại không đúng định dạng");
        }


        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    @Override
    public AddressResponse convertToResponse(Address address) {
        return AddressResponse.builder()
                .accountId(address.getAccount().getId())
                .name(address.getName())
                .phoneNumber(address.getPhoneNumber())
                .specificAddress(address.getSpecificAddress())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .note(address.getNote())
                .defaultAddress(address.getDefaultAddress())
                .build();
    }

    @Override
    public ServiceResult<AddressResponse> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL,mess,null);
    }



}
