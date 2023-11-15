package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.AccountRequest;
import com.backend.dto.request.PasswordRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.request.account.SearchAccountRequest;
import com.backend.dto.response.AccountPageResponse;
import com.backend.dto.response.AccountResponse;
import com.backend.dto.response.RegisterResponse;
import com.backend.dto.response.account.AccountCustomResponse;
import com.backend.entity.Account;
import com.backend.entity.Address;
import com.backend.entity.Cart;
import com.backend.entity.EmailTemplate;
import com.backend.entity.Role;
import com.backend.repository.AccountCustomRepository;
import com.backend.repository.AccountRepository;
import com.backend.repository.AddressRepository;
import com.backend.repository.CartRepository;
import com.backend.repository.EmailRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.IAccountService;
import com.backend.service.IEmailTemplateService;
import com.backend.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
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

    @Autowired
    private AccountCustomRepository accountCustomRepository;

    @Autowired
    private EmailRepository emailRepository;
    private ImageUploadService imageUploadService;

    public AccountServiceImpl(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @Override
    public ServiceResult<RegisterResponse> register(RegisterRequest registerRequest) {
        Optional<Account> accountcheck = accountRepository.findByEmail(registerRequest.getEmail());
        if (accountcheck.isPresent()) {
            return new ServiceResult<>(AppConstant.SUCCESS,
                    "Email đã tồn tại",
                    null);
        } else {
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
            RegisterResponse registerResponse = new RegisterResponse();
            // role 2 là khach hang
            if (account.getRole().getId() == 2) {
                Account accountUpdateCode = accountRepository.findById(account.getId()).get();
                List<Account> listKhachHang = accountRepository.getListByRole(accountUpdateCode.getRole().getId());
                int nextMaKhachHang = listKhachHang.size();
                String maKhachHang = "KH" + String.format("%09d", nextMaKhachHang);
                accountUpdateCode.setCode(maKhachHang);
                accountRepository.save(accountUpdateCode);
                Cart cart = new Cart();
                cart.setAccount(accountUpdateCode);
                cart.setCreatedAt(now);
                cart.setUpdatedAt(now);
                cart.setStatus(1);
                cartRepository.save(cart);
                Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(2);
                if (emailTemplateCheck.isPresent()) {
                    EmailTemplate emailTemplate = emailTemplateCheck.get();
                    String to = account.getEmail();
                    String subject = emailTemplate.getSubject();
                    String mailType = "";
                    String mailContent = emailTemplate.getMailContent() + registerRequest.getPassword();
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                } else {
                    String to = account.getEmail();
                    String subject = "Xin chào bạn đến với store 2003SHOES";
                    String mailType = "";
                    String mailContent = "Mật khẩu của bạn là: " + registerRequest.getPassword();
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                }
            }
            // role 3 là nhan vien
            else if (account.getRole().getId() == 3) {
                Account accountUpdateCode = accountRepository.findById(account.getId()).get();
                List<Account> listKhachHang = accountRepository.getListByRole(accountUpdateCode.getRole().getId());
                int nextMaNhanVien = listKhachHang.size();
                String maNhanVien = "NV" + String.format("%09d", nextMaNhanVien);
                accountUpdateCode.setCode(maNhanVien);
                accountRepository.save(accountUpdateCode);
                Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(1);
                if (emailTemplateCheck.isPresent()) {
                    EmailTemplate emailTemplate = emailTemplateCheck.get();
                    String to = account.getEmail();
                    String subject = emailTemplate.getSubject();
                    String mailType = "";
                    String mailContent = emailTemplate.getMailContent() + registerRequest.getPassword();
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                } else {
                    String to = account.getEmail();
                    String subject = "Chúc mừng đã trở thành nhân viên của store 2003Shoesto store 2003SHOES";
                    String mailType = "";
                    String mailContent = "Mật khẩu của bạn là: " + registerRequest.getPassword();
                    iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                }
            }
            return new ServiceResult<>(AppConstant.SUCCESS,
                    "Registered Successfully",
                    registerResponse.builder()
                            .email(account.getEmail())
                            .name(account.getName())
                            .build());
        }
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
        if (result != null) {
            return result(result);
        } else {
            try {
                Optional<Role> optionalRole = roleRepository.findById(accountRequest.getRoleId());
                if (optionalRole.isPresent()) {
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Role roleId = optionalRole.get();
                    account.setRole(roleId);
                    account.setCreatedAt(now);
                    account.setName(accountRequest.getName());
                    account.setEmail(accountRequest.getEmail());
                    account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
                    account.setAvatar(imageUploadService.uploadImageByName(accountRequest.getAvatar()));
                    account.setStatus(accountRequest.getStatus());
                    if (account.getRole().getId() == 2) {
                        Account accountUpdateCode = accountRepository.findById(account.getId()).get();
                        List<Account> listKhachHang = accountRepository.getListByRole(accountUpdateCode.getRole().getId());
                        int nextMaKhachHang = listKhachHang.size();
                        String maKhachHang = "KH" + String.format("%09d", nextMaKhachHang);
                        accountUpdateCode.setCode(maKhachHang);
                        accountRepository.save(accountUpdateCode);
                        Cart cart = new Cart();
                        cart.setAccount(accountUpdateCode);
                        cart.setCreatedAt(now);
                        cart.setUpdatedAt(now);
                        cart.setStatus(1);
                        cartRepository.save(cart);
                        Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(2);
                        if (emailTemplateCheck.isPresent()) {
                            EmailTemplate emailTemplate = emailTemplateCheck.get();
                            String to = account.getEmail();
                            String subject = emailTemplate.getSubject();
                            String mailType = "";
                            String mailContent = emailTemplate.getMailContent() + accountRequest.getPassword();
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        } else {
                            String to = account.getEmail();
                            String subject = "Xin chào bạn đến với store 2003SHOES";
                            String mailType = "";
                            String mailContent = "Mật khẩu của bạn là: " + accountRequest.getPassword();
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        }
                    }
                    // role 3 là nhan vien
                    else if (account.getRole().getId() == 3) {
                        Account accountUpdateCode = accountRepository.findById(account.getId()).get();
                        List<Account> listKhachHang = accountRepository.getListByRole(accountUpdateCode.getRole().getId());
                        int nextMaNhanVien = listKhachHang.size();
                        String maNhanVien = "NV" + String.format("%09d", nextMaNhanVien);
                        accountUpdateCode.setCode(maNhanVien);
                        accountRepository.save(accountUpdateCode);
                        Optional<EmailTemplate> emailTemplateCheck = emailRepository.checkSendMail(1);
                        if (emailTemplateCheck.isPresent()) {
                            EmailTemplate emailTemplate = emailTemplateCheck.get();
                            String to = account.getEmail();
                            String subject = emailTemplate.getSubject();
                            String mailType = "";
                            String mailContent = emailTemplate.getMailContent() + accountRequest.getPassword();
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        } else {
                            String to = account.getEmail();
                            String subject = "Chúc mừng đã trở thành nhân viên của store 2003Shoesto store 2003SHOES";
                            String mailType = "";
                            String mailContent = "Mật khẩu của bạn là: " + accountRequest.getPassword();
                            iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);
                        }
                    }
                } else {
                    return new ServiceResult<>(AppConstant.FAIL, "Add fail", null);
                }
                account = accountRepository.save(account);
                AccountResponse convertToResponses = convertToResponse(account);
                return new ServiceResult<>(AppConstant.SUCCESS, "Thêm thành công", convertToResponses);
            } catch (Exception e) {
                e.printStackTrace();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
            }

        }
    }

    @Override
    public ServiceResult<AccountPageResponse> findAllAccount(int pageNo, int pageSize) {
        Pageable pageable1 = PageRequest.of(pageNo, pageSize);
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

        return new ServiceResult<>(AppConstant.SUCCESS, "success", accountPageResponse);
    }

    @Override
    public ServiceResult<Account> updateAccount(AccountRequest accountRequest) throws IOException {
        Optional<Account> optionalAccount = accountRepository.findById(accountRequest.getId());
        if (optionalAccount.isPresent()) {
            Account accountId = optionalAccount.get();
            accountId.setId(accountId.getId());
            accountId.setName(accountRequest.getName());
            accountId.setEmail(accountRequest.getEmail());
            accountId.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
            accountId.setAvatar(imageUploadService.uploadImageByName(accountRequest.getAvatar()));

            Account account = accountRepository.save(accountId);

            return new ServiceResult<>(AppConstant.SUCCESS, "update success", account);

        } else {
            return new ServiceResult<>(AppConstant.FAIL, "update fail", null);
        }
    }

    @Override
    public ServiceResult<Account> huyAccount(AccountRequest accountRequest) {
        Optional<Account> accountId = accountRepository.findById(accountRequest.getId());
        if (accountId.isPresent()) {
            Account account = accountId.get();
            account.setStatus(0);
            accountRepository.save(account);
            return new ServiceResult<>(AppConstant.SUCCESS, "huy account Success", null);
        } else {
            return new ServiceResult<>(AppConstant.FAIL, "huy account fail", null);
        }
    }

    @Override
    public ServiceResult<List<Account>> searchNameAccount(String name) {
        List<Account> staffName = accountRepository.searchNameStaff(name);

        if (staffName.isEmpty()) {
            return new ServiceResult<>(AppConstant.FAIL, "khong co nhan vien nay", null);
        } else {
            return new ServiceResult<>(AppConstant.SUCCESS, "success", staffName);
        }
    }

    @Override
    public ServiceResult<Account> findByEmailAccount(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        Account accountEmail = optionalAccount.get();
        String passNew = generateRandomPassword();

        if (optionalAccount.isPresent()) {
            accountEmail.setPassword(passwordEncoder.encode(passNew));
        }

        accountRepository.save(accountEmail);
        String to = accountEmail.getEmail();
        String subject = "Welcome to store bee shoe of group SD-66";
        String mailType = "chao mung nhan vien ";
        String mailContent = "mat khau acccount cua ban la :" + passNew;

        iEmailTemplateService.sendEmail(to, subject, mailType, mailContent);

        return new ServiceResult<>(AppConstant.SUCCESS, "success", accountEmail);
    }

    @Override
    public ServiceResult<String> changePassword(PasswordRequest passwordRequest) {
        Optional<Account> optionalAccount = accountRepository.findById(passwordRequest.getId());

        if (optionalAccount.isPresent()) {
            Account accountId = optionalAccount.get();
            if (passwordRequest.getYourOldPassword().equals(accountId.getPassword())) {
                accountId.setPassword(passwordEncoder.encode(passwordRequest.getEnterNewPassword()));

                if (passwordRequest.getYourOldPassword().equals(passwordRequest.getNewPassword())) {
                    return new ServiceResult<>(AppConstant.SUCCESS, "fail", "Không được trùng với mật khẩu hiện tại");

                } else if (!passwordRequest.getNewPassword().equals(passwordRequest.getEnterNewPassword())) {
                    return new ServiceResult<>(AppConstant.SUCCESS, "fail", "Mật khẩu mới phải trùng với mật khẩu nhập lại");

                } else if (passwordRequest.getNewPassword().equals(passwordRequest.getEnterNewPassword())) {

                    accountRepository.save(accountId);

                    return new ServiceResult<>(AppConstant.SUCCESS, "success", "Chúc mừng bạn đã đổi mật khẩu thành công");
                } else {
                    return new ServiceResult<>(AppConstant.SUCCESS, "fail", "Bạn nhập chưa đúng mật khẩu ");
                }
            } else {
                return new ServiceResult<>(AppConstant.SUCCESS, "fail", "Mật khẩu cũ không trùng với mật khẩu của tài khoản");
            }
        } else {
            return new ServiceResult<>(AppConstant.SUCCESS, "fail", "Đổi mật khẩu thất bại");
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

        if (accountRequest.getRoleId() == null) {
            errorMessages.add("không được để trông role");
        }
        if (accountRequest.getName() == null) {
            errorMessages.add("Name not null");
        }
        if (accountRequest.getEmail() == null) {
            errorMessages.add("email not null");
        }
        if (accountRequest.getPassword() == null) {
            errorMessages.add("password not null");
        }
        if (accountRequest.getStatus() == null) {
            errorMessages.add("status not null");
        }


        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    public ServiceResult<AccountResponse> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL, mess, null);
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

    public AccountCustomResponse convertPage(Object[] object) {
        AccountCustomResponse accountCustomResponse = new AccountCustomResponse();
        accountCustomResponse.setId(((BigInteger) object[0]).longValue());
        accountCustomResponse.setCode((String) object[1]);
        accountCustomResponse.setName((String) object[2]);
        accountCustomResponse.setEmail((String) object[3]);
        accountCustomResponse.setAvatar((String) object[4]);
        accountCustomResponse.setRoleId(((BigInteger) object[5]).longValue());
        accountCustomResponse.setStatus((Integer) object[6]);
        return accountCustomResponse;
    }

    @Override
    public Page<AccountCustomResponse> searchAccount(SearchAccountRequest searchAccountRequest) {
        Pageable pageable = PageRequest.of(searchAccountRequest.getPage(), searchAccountRequest.getSize());
        if (searchAccountRequest.getName() != null) {
            String name = searchAccountRequest.getName();
            name = name.replaceAll("\\\\", "\\\\\\\\");
            name = name.replaceAll("%", "\\\\%");
            name = name.replaceAll("_", "\\\\_");
            searchAccountRequest.setName(name);
        }
        if (searchAccountRequest.getEmail() != null) {
            String email = searchAccountRequest.getEmail();
            email = email.replaceAll("\\\\", "\\\\\\\\");
            email = email.replaceAll("%", "\\\\%");
            email = email.replaceAll("_", "\\\\_");
            searchAccountRequest.setEmail(email);
        }
        Page<Object> objects = accountCustomRepository.doSearch(
                pageable,
                searchAccountRequest.getName(),
                searchAccountRequest.getEmail(),
                searchAccountRequest.getRole(),
                searchAccountRequest.getStatusAccount()
        );
        List<AccountCustomResponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            AccountCustomResponse accountCustomResponse = convertPage(result);
            list.add(accountCustomResponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }
}
