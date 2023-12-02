package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.AddressRequest;
import com.backend.dto.request.account.AccountAddress;
import com.backend.dto.response.AddressResponse;
import com.backend.dto.response.shoedetail.ListSizeOfShoe;
import com.backend.entity.Account;
import com.backend.entity.Address;
import com.backend.repository.AccountRepository;
import com.backend.repository.AddressRepository;
import com.backend.service.IAddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                   address.setDefaultAddress("0");
//                   if(address.getDefaultAddress().equals("0")){
//                       return new ServiceResult<>(AppConstant.SUCCESS, "Add that bai chi duoc 1 dia chi mac dinh", null);
//                   }

                       address = addressRepository.save(address);
                       AddressResponse convertAddressResponse = convertToResponse(address);

                       return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertAddressResponse);

               }else{
                  return new ServiceResult<>(AppConstant.FAIL,"Add fail",null);
               }

           }catch (Exception e){
               e.printStackTrace();
               return new ServiceResult<>(AppConstant.BAD_REQUEST,e.getMessage(),null);
           }
        }
    }

    @Override
    public ServiceResult<Address> updateAddress(AddressRequest addressRequest) {
        Optional<Address> addressId = addressRepository.findById(addressRequest.getId());
        Optional<Account> accountId = accountRepository.findById(addressRequest.getAccountId());
        Account account = accountId.get();
        if (addressId.isPresent()) {
            Address addressExist = addressId.get();
            addressExist.setId(addressExist.getId());
            addressExist.setAccount(account);
            addressExist.setName(addressRequest.getName());
            addressExist.setPhoneNumber(addressRequest.getPhoneNumber());
            addressExist.setSpecificAddress(addressRequest.getSpecificAddress());
            addressExist.setWard(addressRequest.getWard());
            addressExist.setDistrict(addressRequest.getDistrict());
            addressExist.setProvince(addressRequest.getProvince());
            addressExist.setNote(addressRequest.getNote());

            Address address = addressRepository.save(addressExist);
            return new ServiceResult<>(AppConstant.SUCCESS, "Update success", address);

        }else{
            return new ServiceResult<>(AppConstant.BAD_REQUEST,"Update fail",null);
        }
    }

    @Override
    public ServiceResult<Address> updateDefaultAddress(AddressRequest addressRequest) {
        Optional<Address> addressId = addressRepository.findById(addressRequest.getId());
        Optional<Account> accountId = accountRepository.findById(addressRequest.getAccountId());
        List<Address> addressList = addressRepository.findAddressesByAccount_Id(addressRequest.getAccountId());
        Account account = accountId.get();
        if (addressId.isPresent() && accountId.isPresent()) {
            Address addressExist = addressId.get();
            addressExist.setId(addressExist.getId());

            boolean hasAddressWithOneDefault = addressList.stream()
                    .anyMatch(address -> "1".equals(address.getDefaultAddress()));

            System.out.println(hasAddressWithOneDefault);

            if (hasAddressWithOneDefault) {
                return new ServiceResult<>(AppConstant.BAD_REQUEST, "chi duoc 1 dia chi mac dinh", null);
            } else {
                addressExist.setAccount(account);
                addressExist.setDefaultAddress("1");
                Address address = addressRepository.save(addressExist);

                return new ServiceResult<>(AppConstant.SUCCESS, "Update success", address);
            }

        }else{
            return new ServiceResult<>(AppConstant.BAD_REQUEST,"Update fail",null);
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
            addressResponse.setDefaultAddress(address.getDefaultAddress().equals("1") ? "this is a defaultAddress": "");

            addressResponses.add(addressResponse);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,
                "Successfully retrieved",
                    addressResponses);
    }



    @Override
    public ServiceResult<Address> deleteAddress(AddressRequest addressRequest) {
        Optional<Address> optionalAddress = addressRepository.findById(addressRequest.getId());
        if (optionalAddress.isPresent()){
            Address address = optionalAddress.get();
            addressRepository.save(address);
            return new ServiceResult<>(AppConstant.SUCCESS,"delete Success",null);
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"Id not exist",null);
        }
    }

    @Override
    public ServiceResult<List<AccountAddress>> getOneAddressByAccountId(Long id) {
        List<Object[]> addressList = addressRepository.getOneAddressByAccountId(id);
        List<AccountAddress> addressResponsesList = new ArrayList<>();

        for (Object[] record: addressList){
            AccountAddress accountAddress = new AccountAddress();

            accountAddress.setIdAccount( (BigInteger) record[0]);
            accountAddress.setIdRole((BigInteger) record[1]);
            accountAddress.setNameAccount((String) record[2]);
            accountAddress.setCode((String) record[3]);
            accountAddress.setPassword((String) record[4]);
            accountAddress.setAvatar((String) record[5]);
            accountAddress.setFormattedDatesCreateTime((Timestamp) record[6]);
            accountAddress.setFormattedDatesUpdateTime((Timestamp) record[7]);
            accountAddress.setStatus((Integer) record[8]);
            accountAddress.setNameAddress((String) record[9]);
            accountAddress.setPhoneNumber((String) record[10]);
            accountAddress.setSpecificAddress((String) record[11]);
            accountAddress.setWard((String) record[12]);
            accountAddress.setDistrict((Integer) record[13]);
            accountAddress.setProvince((Integer) record[14]);
            accountAddress.setNote((String) record[15]);
            accountAddress.setDefaultAddress((String) record[16]);

            addressResponsesList.add(accountAddress);
        }

        if (addressResponsesList.size()<0){
            return new ServiceResult<>(AppConstant.SUCCESS,"fail",null);
        }else{
            return new ServiceResult<>(AppConstant.SUCCESS,"success",addressResponsesList);
        }
    }

    @Override
    public String validateAddress(AddressRequest addressRequest) {
        List<String> errorMessages = new ArrayList<>();

        if (addressRequest.getAccountId() == null && addressRequest.getDefaultAddress().isBlank()){
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
