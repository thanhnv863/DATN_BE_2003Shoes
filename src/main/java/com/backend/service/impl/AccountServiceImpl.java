package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.AccountRequest;
import com.backend.dto.request.EmailRequest;
import com.backend.dto.request.PasswordRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AccountPageResponse;
import com.backend.dto.response.AccountResponse;
import com.backend.dto.response.RegisterResponse;
import com.backend.entity.Address;
import com.backend.entity.Cart;
import com.backend.entity.Role;
import com.backend.entity.Account;
import com.backend.repository.AccountRepository;
import com.backend.repository.AddressRepository;
import com.backend.repository.CartRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.IAccountService;
import com.backend.service.IEmailTemplateService;
import com.backend.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailTemplateService iEmailTemplateService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AddressRepository addressRepository;

    private ImageUploadService imageUploadService;

    public AccountServiceImpl(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @Override
    public ServiceResult<RegisterResponse> register(RegisterRequest registerRequest) {
        Account account = new Account();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        account.setName(registerRequest.getName());
        account.setEmail(registerRequest.getEmail());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        account.setStatus(1);
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(Role.builder().id(registerRequest.getIdRole()).build());
        account = accountRepository.save(account);
        Cart cart = new Cart();
        cart.setAccount(account);
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
        cart.setStatus(1);
        cartRepository.save(cart);
        RegisterResponse registerResponse = new RegisterResponse();

        String to = registerRequest.getEmail();
        String subject = "Welcome to store 2003SHOES";
        String mailType = "chao mung nhan vien ";
        String mailContent = "Mật khẩu tài khoản của bạn là  :"+registerRequest.getPassword();

        iEmailTemplateService.sendEmail(to,subject,mailType,mailContent);
        return new ServiceResult<>(AppConstant.SUCCESS,
                "Registered Successfully",
                registerResponse.builder()
                        .email(account.getEmail())
                        .name(account.getName())
                        .build());
    }

    @Override
    public Boolean exitsEmail(RegisterRequest registerRequest) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(registerRequest.getEmail());
        if (optionalAccount.isPresent()) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public ServiceResult<AccountResponse> addAccount(AccountRequest accountRequest) {
        Account account = new Account();
        String result = validateStaff(accountRequest);

        if(result != null){
            return result(result);
        }else{
            try{
                Optional<Role> optionalRole = roleRepository.findById(accountRequest.getRoleId());
                if (optionalRole.isPresent()){
                    Role roleId = optionalRole.get();
                    account.setRole(roleId);
                    account.setName(accountRequest.getName());
                    account.setEmail(accountRequest.getEmail());
                    account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
                    account.setAvatar(imageUploadService.uploadImageByName(accountRequest.getAvatar()));
                    account.setStatus(accountRequest.getStatus());
                }else{
                    return new ServiceResult<>(AppConstant.FAIL,"Add fail",null);
                }

                account = accountRepository.save(account);
                AccountResponse convertToResponses = convertToResponse(account);

                return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertToResponses);

            }catch (Exception e){
                e.printStackTrace();
                return new ServiceResult<>(AppConstant.BAD_REQUEST,e.getMessage(),null);
            }

        }
    }

    @Override
    public ServiceResult<AccountPageResponse> findAllAccount(int pageNo, int pageSize) {
        Pageable pageable1 = PageRequest.of(pageNo,pageSize);
        Page<Account> pageAccount = accountRepository.getAllAccount(pageable1);
        List<Address> listAddress = addressRepository.findAll();

        AccountPageResponse accountPageResponse = new AccountPageResponse();
        accountPageResponse.setAccounts(pageAccount.getContent());
        accountPageResponse.setAddresses(listAddress);
        accountPageResponse.setTotalPages(pageAccount.getTotalPages());
        accountPageResponse.setTotalElements(pageAccount.getTotalElements());
        accountPageResponse.setNumberOfElements(pageAccount.getNumberOfElements());
        accountPageResponse.setSize(pageAccount.getSize());
        accountPageResponse.setLast(pageAccount.isLast());
        accountPageResponse.setFirst(pageAccount.isFirst());

        return new ServiceResult<>(AppConstant.SUCCESS,"success",accountPageResponse);
    }

    @Override
    public ServiceResult<Account> updateAccount(AccountRequest accountRequest) throws IOException {
        Optional<Account> optionalAccount = accountRepository.findById(accountRequest.getId());
        if (optionalAccount.isPresent()){
            Account accountId = optionalAccount.get();
            accountId.setId(accountId.getId());
            accountId.setName(accountRequest.getName());
            accountId.setEmail(accountRequest.getEmail());
            accountId.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
            accountId.setAvatar(imageUploadService.uploadImageByName(accountRequest.getAvatar()));

            Account account = accountRepository.save(accountId);

            return new ServiceResult<>(AppConstant.SUCCESS,"update success",account);

        }else{
            return new ServiceResult<>(AppConstant.FAIL,"update fail",null);
        }
    }

    @Override
    public ServiceResult<Account> huyAccount(AccountRequest accountRequest) {
        Optional<Account> accountId = accountRepository.findById(accountRequest.getId());
        if (accountId.isPresent()){
            Account account = accountId.get();
            accountRepository.save(account);
            return new ServiceResult<>(AppConstant.SUCCESS,"delete Success",null);
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"delete fail",null);
        }
    }

    @Override
    public ServiceResult<List<Account>> searchNameAccount(String name) {
        List<Account> staffName = accountRepository.searchNameStaff(name);

        if (staffName.isEmpty()){
            return new ServiceResult<>(AppConstant.FAIL, "khong co nhan vien nay", null);
        }else{
            return new ServiceResult<>(AppConstant.SUCCESS, "success", staffName);
        }
    }

    @Override
    public ServiceResult<Account> findByEmailAccount(String email) {
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

    @Override
    public ServiceResult<String> changePassword(PasswordRequest passwordRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(passwordRequest.getId());
        if (optionalAccount.isPresent()) {
            Account accountId = optionalAccount.get();
            accountId.setPassword(passwordRequest.getYourOldPassword());
            accountId.setPassword(passwordRequest.getNewPassword());
            accountId.setPassword(passwordRequest.getEnterNewPassword());

            if(passwordRequest.getYourOldPassword().equals(passwordRequest.getNewPassword())){
                return new ServiceResult<>(AppConstant.SUCCESS,"fail","không được trùng với mật khẩu hiện tại");
             } else if (!passwordRequest.getNewPassword().equals(passwordRequest.getEnterNewPassword())){
                return new ServiceResult<>(AppConstant.SUCCESS,"fail","mật khẩu mới phải trùng với mật khẩu nhập lại");
            } else if(passwordRequest.getNewPassword().equals(passwordRequest.getEnterNewPassword())){
                accountRepository.save(accountId);
                return new ServiceResult<>(AppConstant.SUCCESS,"success","chu mung ban da doi mat khau thanh cong");
            }else{
                return new ServiceResult<>(AppConstant.SUCCESS,"fail","bạn nhập chưa đúng mật khẩu ");
            }

        }else{
            return new ServiceResult<>(AppConstant.SUCCESS,"fail","doi mat khau that bai");
        }

    }


    public AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .roleId(account.getRole().getId())
                .name(account.getName())
                .email(account.getEmail())
                .password(account.getPassword())
                .avatar(account.getAvatar())
                .status(account.getStatus())
                .build();
    }

    public String validateStaff(AccountRequest accountRequest) {
        List<String> errorMessages = new ArrayList<>();

        if (accountRequest.getRoleId() == null){
            errorMessages.add("không được để trông role");
        }
        if(accountRequest.getName() == null){
            errorMessages.add("Name not null");
        }
        if(accountRequest.getEmail() == null){
            errorMessages.add("email not null");
        }
        if(accountRequest.getPassword() == null){
            errorMessages.add("password not null");
        }
        if(accountRequest.getStatus() == null){
            errorMessages.add("status not null");
        }



        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    public ServiceResult<AccountResponse> result(String mess) {
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
