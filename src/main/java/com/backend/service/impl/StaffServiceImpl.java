package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.StaffRequest;
import com.backend.dto.response.StaffResponse;
import com.backend.entity.Account;
import com.backend.entity.Address;
import com.backend.entity.Role;
import com.backend.repository.AccountRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.IEmailTemplateService;
import com.backend.service.IStaffServices;
import com.backend.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements IStaffServices {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IEmailTemplateService iEmailTemplateService;

    private PasswordEncoder passwordEncoder;

    private ImageUploadService imageUploadService;

    public StaffServiceImpl(PasswordEncoder passwordEncoder, ImageUploadService imageUploadService) {
        this.passwordEncoder = passwordEncoder;
        this.imageUploadService = imageUploadService;
    }

    @Override
    public ServiceResult<StaffResponse> addStaff(StaffRequest staffRequest) {
        Account account = new Account();
        String result = validateStaff(staffRequest);

        if(result != null){
            return result(result);
        }else{
            try{
                Optional<Role> optionalRole = roleRepository.findById(staffRequest.getRoleId());
                if (optionalRole.isPresent()){
                    Role roleId = optionalRole.get();
                    account.setRole(roleId);
                    account.setName(staffRequest.getName());
                    account.setEmail(staffRequest.getEmail());
                    account.setPassword(passwordEncoder.encode(staffRequest.getPassword()));
                    account.setAvatar(imageUploadService.uploadImageByName(staffRequest.getAvatar()));
                    account.setStatus(staffRequest.getStatus());
                }else{
                    return new ServiceResult<>(AppConstant.FAIL,"Add fail",null);
                }

                account = accountRepository.save(account);
                StaffResponse convertToResponses = convertToResponse(account);

                return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertToResponses);

            }catch (Exception e){
                e.printStackTrace();
                return new ServiceResult<>(AppConstant.BAD_REQUEST,e.getMessage(),null);
            }

        }
    }

    @Override
    public ServiceResult<List<Account>> findAllStaff() {

        List<Account> account = accountRepository.findAll();

        if (account.size() < 0){
            return new ServiceResult<>(AppConstant.FAIL,"fail",null);
        }else{
            return new ServiceResult<>(AppConstant.SUCCESS,"success",account);
        }

    }

    @Override
    public ServiceResult<Account> updateStaff(StaffRequest staffRequest) throws IOException {
        Optional<Account> optionalAccount = accountRepository.findById(staffRequest.getId());
        if (optionalAccount.isPresent()){
            Account accountId = optionalAccount.get();
            accountId.setId(accountId.getId());
            accountId.setName(staffRequest.getName());
            accountId.setEmail(staffRequest.getEmail());
            accountId.setPassword(passwordEncoder.encode(staffRequest.getPassword()));
            accountId.setAvatar(imageUploadService.uploadImageByName(staffRequest.getAvatar()));

            Account account = accountRepository.save(accountId);

            return new ServiceResult<>(AppConstant.SUCCESS,"update success",account);

        }else{
            return new ServiceResult<>(AppConstant.FAIL,"update fail",null);
        }
    }

    @Override
    public ServiceResult<Account> deleteAddress(StaffRequest staffRequest) {
        Optional<Account> accountId = accountRepository.findById(staffRequest.getId());
        if (accountId.isPresent()){
            Account account = accountId.get();
            accountRepository.save(account);
            return new ServiceResult<>(AppConstant.SUCCESS,"delete Success",null);
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"delete fail",null);
        }
    }

    @Override
    public ServiceResult<List<Account>> searchNameStaff(String name) {
        List<Account> staffName = accountRepository.searchNameStaff(name);

        if (staffName.isEmpty()){
            return new ServiceResult<>(AppConstant.FAIL, "khong co nhan vien nay", null);
        }else{
            return new ServiceResult<>(AppConstant.SUCCESS, "success", staffName);
        }
    }

    @Override
    public ServiceResult<Account> findByEmailStaff(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        Account accountEmail = optionalAccount.get();
        String passNew = generateRandomPassword();

        if (optionalAccount.isPresent()){
            accountEmail.setPassword(passwordEncoder.encode(passNew));
        }

        accountRepository.save(accountEmail);

        String to = accountEmail.getEmail();
        String subject = "Welcome to store bee shoe of group SD-66";
        String mailType = "chao mung nhan vien ";
        String mailContent = "mat khau acccount cua ban la :"+passNew;

        iEmailTemplateService.sendEmail(to,subject,mailType,mailContent);

        return new ServiceResult<>(AppConstant.SUCCESS,"success",accountEmail);
    }


    public StaffResponse convertToResponse(Account account) {
        return StaffResponse.builder()
                .roleId(account.getRole().getId())
                .name(account.getName())
                .email(account.getEmail())
                .password(account.getPassword())
                .avatar(account.getAvatar())
                .status(account.getStatus())
                .build();
    }

    public String validateStaff(StaffRequest staffRequest) {
        List<String> errorMessages = new ArrayList<>();

        if (staffRequest.getRoleId() == null){
            errorMessages.add("không được để trông role");
        }
        if(staffRequest.getName() == null){
            errorMessages.add("Name not null");
        }
        if(staffRequest.getEmail() == null){
            errorMessages.add("email not null");
        }
        if(staffRequest.getPassword() == null){
            errorMessages.add("password not null");
        }
        if(staffRequest.getStatus() == null){
            errorMessages.add("status not null");
        }



        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    public ServiceResult<StaffResponse> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL,mess,null);
    }

    public String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }
}
