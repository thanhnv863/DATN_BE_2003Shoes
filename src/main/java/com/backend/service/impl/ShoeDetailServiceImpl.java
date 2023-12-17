package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.ListSizeOfShoeReq;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailId;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.ResponseImport;
import com.backend.dto.response.ShoeAndShoeDetailResponse;
import com.backend.dto.response.shoedetail.ListSizeExits;
import com.backend.dto.response.shoedetail.ListSizeOfShoe;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Color;
import com.backend.entity.Image;
import com.backend.entity.Shoe;
import com.backend.entity.ShoeDetail;
import com.backend.entity.Size;
import com.backend.entity.Sole;
import com.backend.entity.Thumbnail;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ColorRepository;
import com.backend.repository.ImageRepository;
import com.backend.repository.ShoeDetailCustomRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.repository.ShoeRepository;
import com.backend.repository.SizeRepository;
import com.backend.repository.SoleRepository;
import com.backend.repository.ThumbnailRepository;
import com.backend.service.IShoeDetailService;
import com.backend.service.ImageUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoeDetailServiceImpl implements IShoeDetailService {

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private ShoeDetailRepository shoeDetailRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandCategory;

    @Autowired
    private SoleRepository soleRepository;

    @Autowired
    private ShoeDetailCustomRepository shoeDetailCustomRepository;

    @Override
    public ServiceResult<Shoe> resultValidate(String mess) {
        return new ServiceResult<>(AppConstant.FAIL, mess, null);
    }

    @Override
    public Object searchById(BigInteger id) {
        Object result = shoeDetailCustomRepository.getOne(id);
        Object[] resultArray = (Object[]) result;
        ResultItem resultItem = convertToPage(resultArray);
        return resultItem;
    }

    @Override
    public ServiceResult<ShoeDetail> activeShoeDetail(ShoeDetailId shoeDetailId) {
        Optional<ShoeDetail> shoeDetailOptional = shoeDetailRepository.findById(shoeDetailId.getIdSD());
        if (shoeDetailOptional.isPresent()) {
            ShoeDetail shoeDetail = shoeDetailOptional.get();
            shoeDetail.setStatus(1);
            shoeDetailRepository.save(shoeDetail);
            return new ServiceResult<>(AppConstant.SUCCESS, "The Shoe active succesfully!", null);
        } else {
            return new ServiceResult<>(AppConstant.BAD_REQUEST, "The Shoe not found!", null);
        }
    }

    @Override
    public ServiceResult<ShoeDetail> inActiveShoeDetail(ShoeDetailId shoeDetailId) {
        Optional<ShoeDetail> shoeDetailOptional = shoeDetailRepository.findById(shoeDetailId.getIdSD());
        if (shoeDetailOptional.isPresent()) {
            ShoeDetail shoeDetail = shoeDetailOptional.get();
            shoeDetail.setStatus(0);
            shoeDetailRepository.save(shoeDetail);
            return new ServiceResult<>(AppConstant.SUCCESS, "The Shoe inactive succesfully!", null);
        } else {
            return new ServiceResult<>(AppConstant.BAD_REQUEST, "The Shoe not found!", null);
        }
    }

    @Override
    public List<ListSizeOfShoe> getListSizeOfShoe(ListSizeOfShoeReq listSizeOfShoeReq) {
        List<Object[]> list =
                shoeDetailCustomRepository.getListSizeByShoeNameId(listSizeOfShoeReq.getIdShoe(), listSizeOfShoeReq.getIdColor());
        List<ListSizeOfShoe> listSizeOfShoe = list.stream().map(row -> {
            Float nameOfSize = (Float) row[0];
            BigInteger shoeId = (BigInteger) row[1];
            String code = (String) row[2];
            BigInteger shoeDetailId = (BigInteger) row[3];
            return new ListSizeOfShoe(nameOfSize, shoeId, code, shoeDetailId);
        }).collect(Collectors.toList());
        return listSizeOfShoe;
    }

    @Override
    public List<?> getListSizeExits(ListSizeOfShoeReq listSizeOfShoeReq) {
        List<Object[]> list =
                shoeDetailCustomRepository.getListSizeExits(listSizeOfShoeReq.getIdShoe(), listSizeOfShoeReq.getIdColor());
//        List<ListSizeExits> listSizeExits = list.stream().map(row -> {
//            Long sizeId = (Long) row[0];
//            return new ListSizeExits(sizeId);
//        }).collect(Collectors.toList());

        return list;
    }

    public ServiceResult<?> getListSizeExitss(ListSizeOfShoeReq listSizeOfShoeReq) {
        List<Object[]> list =
                shoeDetailCustomRepository.getListSizeExits(listSizeOfShoeReq.getIdShoe(), listSizeOfShoeReq.getIdColor());
//        List<ListSizeExits> listSizeExits = list.stream().map(row -> {
//            Long sizeId = (Long) row[0];
//            return new ListSizeExits(sizeId);
//        }).collect(Collectors.toList());

        return new ServiceResult<>(AppConstant.SUCCESS,"hi",list);
    }
    @Override
    public List<ResultItem> getShoeDetailsCustom(SearchShoeDetailRequest searchShoeDetailRequest) {
        if (searchShoeDetailRequest.getShoe() != null) {
            String shoe = searchShoeDetailRequest.getShoe();
            shoe = shoe.replaceAll("\\\\", "\\\\\\");
            shoe = shoe.replaceAll("%", "\\\\\\%");
            shoe = shoe.replaceAll("_", "\\\\\\_");
            searchShoeDetailRequest.setShoe(shoe);
        }
        List<Object> objectList = shoeDetailCustomRepository.getListByCustom(
                searchShoeDetailRequest.getShoe(),
                searchShoeDetailRequest.getSize(),
                searchShoeDetailRequest.getCategory(),
                searchShoeDetailRequest.getBrand(),
                searchShoeDetailRequest.getSole(),
                searchShoeDetailRequest.getColor(),
                searchShoeDetailRequest.getMinPrice(),
                searchShoeDetailRequest.getMaxPrice(),
                searchShoeDetailRequest.getStatus()
        );
        List<ResultItem> list = new ArrayList<>();
        for (Object object : objectList) {
            Object[] result = (Object[]) object;
            ResultItem resultItem = convertToPage(result);
            list.add(resultItem);
        }
        return list;
    }

    @Override
    public List<ResultItem> getTop4BestSale() {
        List<Object[]> list =
                shoeDetailCustomRepository.getListTop4BestSale();
        System.out.println(list);
        List<ResultItem> listResult = new ArrayList<>();
        for (Object[] object : list) {
            ResultItem resultItem = convertToPage(object);
            listResult.add(resultItem);
        }
        return listResult;
    }

    @Override
    public List<ResultItem> getTop4News() {
        List<Object[]> list =
                shoeDetailCustomRepository.getListTop4Newest();
        System.out.println(list);
        List<ResultItem> listResult = new ArrayList<>();
        for (Object[] object : list) {
            ResultItem resultItem = convertToPage(object);
            listResult.add(resultItem);
        }
        return listResult;
    }

    @Override
    public List<ResultItem> getVersionOfShoe(Long idShoe) {
        List<Object[]> list =
                shoeDetailCustomRepository.getListVersionOfShoe(idShoe);
        List<ResultItem> listResult = new ArrayList<>();
        for (Object[] object : list) {
            ResultItem resultItem = convertToPage(object);
            listResult.add(resultItem);
        }
        return listResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Shoe> updateShoeDetail(ShoeDetailRequestUpdate shoeDetailRequestUpdate) {
        String result = validateUpdateShoeDetail(shoeDetailRequestUpdate);
        if (result != null) {
            return resultValidate(result);
        } else {
            try {
                Optional<ShoeDetail> shoeDetailOptional = shoeDetailRepository.findById(shoeDetailRequestUpdate.getId());
                if (shoeDetailOptional.isPresent()) {
                    ShoeDetail shoeDetail = shoeDetailOptional.get();
                    Optional<Shoe> optionalShoe = shoeRepository.findById(shoeDetailRequestUpdate.getShoe().getId());
                    Optional<Color> optionalColor = colorRepository.findById(shoeDetailRequestUpdate.getColor().getId());
                    Optional<Category> optionalCategory = categoryRepository.findById(shoeDetailRequestUpdate.getCategory().getId());
                    Optional<Brand> optionalBrand = brandCategory.findById(shoeDetailRequestUpdate.getBrand().getId());
                    Optional<Size> optionalSize = sizeRepository.findById(shoeDetailRequestUpdate.getSize().getId());
                    Optional<Sole> optionalSole = soleRepository.findById(shoeDetailRequestUpdate.getSole().getId());

                    List<String> errors = new ArrayList<>();

                    if (!optionalShoe.isPresent()) {
                        errors.add("Shoe không tồn tại");
                    }

                    if (!optionalSize.isPresent()) {
                        errors.add("Size không tồn tại");
                    }

                    if (!optionalSole.isPresent()) {
                        errors.add("Sole không tồn tại");
                    }

                    if (!optionalBrand.isPresent()) {
                        errors.add("Brand không tồn tại");
                    }

                    if (!optionalCategory.isPresent()) {
                        errors.add("Loại giày không tồn tại");
                    }

                    if (!optionalColor.isPresent()) {
                        errors.add("Màu sắc không tồn tại");
                    }

                    if (!errors.isEmpty()) {
                        throw new RuntimeException(String.join(", ", errors));
                    }

                    shoeDetail.setShoe(optionalShoe.get());
                    shoeDetail.setColor(optionalColor.get());
                    shoeDetail.setCategory(optionalCategory.get());
                    shoeDetail.setBrand(optionalBrand.get());
                    shoeDetail.setSize(optionalSize.get());
                    shoeDetail.setSole(optionalSole.get());
                    shoeDetail.setPriceInput(shoeDetailRequestUpdate.getPriceInput());
                    shoeDetail.setQuantity(shoeDetailRequestUpdate.getQuantity());
                    shoeDetail.setCreatedAt(shoeDetailRequestUpdate.getCreatedAt());
                    shoeDetail.setUpdatedAt(new Date());
                    shoeDetail.setStatus(1);
                    shoeDetail.setQrCode(shoeDetailRequestUpdate.getQrCode());
                    shoeDetail.setCreatedBy(shoeDetailRequestUpdate.getCreatedBy());
                    shoeDetail.setUpdatedBy(shoeDetailRequestUpdate.getUpdatedBy());

                    Float sizeName = optionalSize.get().getName();
                    String shoeName = optionalShoe.get().getName();
                    String colorName = optionalColor.get().getName();
                    shoeDetail.setCode(shoeName.toLowerCase() + " - " + colorName.toLowerCase() + " - " + sizeName);


                    if (shoeDetailRequestUpdate.getThumbnails() == null || shoeDetailRequestUpdate.getThumbnails().size() <= 0) {
                        throw new RuntimeException("Thumbnail không được để trống");
                    } else {
                        saveThumbnails(shoeDetail, shoeDetailRequestUpdate.getThumbnails());
                    }
                    if (shoeDetailRequestUpdate.getImages() == null || shoeDetailRequestUpdate.getImages().size() <= 0) {
                        throw new RuntimeException("Images không được để trống");
                    } else {
                        saveImages(shoeDetail, shoeDetailRequestUpdate.getImages());
                    }
                    shoeDetailRepository.save(shoeDetail);
                    return new ServiceResult<>(AppConstant.SUCCESS, "Shoe update successfully", null);
                } else {
                    return new ServiceResult<>(AppConstant.NOT_FOUND, "ShoeDetail not found", null);
                }
//                    String qrCode = generateQrCode(ShoeDetail.builder().id(shoeDetail.getId()).build());
//                    saveQrCode(shoeDetail, qrCode);

            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> updateQtyShoeDetail(List<ShoeDetailRequestUpdate> shoeDetailRequestUpdateList) {
        try {
            for (ShoeDetailRequestUpdate update : shoeDetailRequestUpdateList) {
                Long id = update.getId();
                Integer qty = update.getQuantity();
                ShoeDetail shoeDetail = shoeDetailRepository.findById(id).orElse(null);
                if (shoeDetail != null) {
                    int currentQuantity = shoeDetail.getQuantity();
                    if (currentQuantity < qty) {
                        throw new RuntimeException("Số lượng không đủ");
                    }
                    shoeDetail.setQuantity(currentQuantity - qty);
                    shoeDetailRepository.save(shoeDetail);
                } else {
                    throw new RuntimeException("Sản phẩm không tồn tại");
                }
            }
            return new ServiceResult<>(AppConstant.SUCCESS, "Cập nhật thành công", null);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest) {
        String result = validateShoeDetail(shoeDetailRequest);
        if (result != null) {
            return resultValidate(result);
        } else {
            try {
                for (ShoeDetail requestShoeDetail : shoeDetailRequest.getShoeDetailList()) {
                    ShoeDetail shoeDetail = createShoeDetail(requestShoeDetail);
                    if (requestShoeDetail.getThumbnails() == null || requestShoeDetail.getThumbnails().size() <= 0) {
                        throw new RuntimeException("Thumbnail không được để trống");
                    } else {
                        saveThumbnails(shoeDetail, requestShoeDetail.getThumbnails());
                    }
                    if (requestShoeDetail.getImages() == null || requestShoeDetail.getImages().size() <= 0) {
                        throw new RuntimeException("Images không được để trống");
                    } else {
                        saveImages(shoeDetail, requestShoeDetail.getImages());
                    }
                    String qrCode = generateQrCode(ShoeDetail.builder().id(shoeDetail.getId()).build());
                    saveQrCode(shoeDetail, qrCode);
                }
                return new ServiceResult<>(AppConstant.SUCCESS, "Shoe added successfully", null);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
            }
        }
    }

    private ResultItem convertToPage(Object[] objects) {
        ResultItem resultItem = new ResultItem();
        resultItem.setId(((BigInteger) objects[0]).longValue());
        resultItem.setNameShoe((String) objects[1]);
        resultItem.setSize((Float) objects[2]);
        resultItem.setCategory((String) objects[3]);
        resultItem.setBrand((String) objects[4]);
        resultItem.setSole((String) objects[5]);
        resultItem.setColor((String) objects[6]);
        resultItem.setCode((String) objects[7]);
        resultItem.setQrCode((String) objects[8]);
        resultItem.setPriceInput((BigDecimal) objects[9]);
        resultItem.setQty((Integer) objects[10]);
        resultItem.setCreatedAt((Date) objects[11]);
        resultItem.setUpdatedAt((Date) objects[12]);
        resultItem.setStatus((Integer) objects[13]);
        resultItem.setThumbnail((String) objects[14]);

        String inputString = (String) objects[15];
        List<String> listOfStrings = Arrays.stream(inputString.split(","))
                .collect(Collectors.toList());
        resultItem.setImages(listOfStrings);

        return resultItem;
    }

    @Override
    public Page<ResultItem> searchShoeDetail(SearchShoeDetailRequest searchShoeDetailRequest) {
        Pageable pageable = PageRequest.of(searchShoeDetailRequest.getPage() - 1, searchShoeDetailRequest.getPageSize());
        if (searchShoeDetailRequest.getShoe() != null) {
            String shoe = searchShoeDetailRequest.getShoe();
            shoe = shoe.replaceAll("\\\\", "\\\\\\");
            shoe = shoe.replaceAll("%", "\\\\\\%");
            shoe = shoe.replaceAll("_", "\\\\\\_");
            searchShoeDetailRequest.setShoe(shoe);
        }
        Page<Object> objectPage = shoeDetailCustomRepository.doSearch(pageable,
                searchShoeDetailRequest.getShoe(),
                searchShoeDetailRequest.getSizeList(),
                searchShoeDetailRequest.getCategoryList(),
                searchShoeDetailRequest.getBrandList(),
                searchShoeDetailRequest.getSoleList(),
                searchShoeDetailRequest.getColorList(),
                searchShoeDetailRequest.getMinPrice(),
                searchShoeDetailRequest.getMaxPrice(),
                searchShoeDetailRequest.getSort()
        );
        List<ResultItem> list = new ArrayList<>();
        for (Object object : objectPage) {
            Object[] result = (Object[]) object;
            ResultItem resultItem = convertToPage(result);
            list.add(resultItem);
        }
        return new PageImpl<>(list, pageable, objectPage.getTotalElements());
    }

    private void saveQrCode(ShoeDetail shoeDetail, String qrCode) {
        shoeDetail.setQrCode(qrCode);
        shoeDetailRepository.save(shoeDetail);
    }

    private String generateQrCode(ShoeDetail shoeDetail) {
        String prettyData = prettyObj(shoeDetail);
        String qrCode = processingGenerateQrCode(prettyData, 300, 300);
        return qrCode;
    }

    private String prettyObj(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String processingGenerateQrCode(String data, int width, int height) {
        StringBuilder result = new StringBuilder();
        if (!data.isEmpty()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);

                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ImageIO.write(bufferedImage, "png", os);

                result.append("data:image/png;base64,");
                result.append(new String(Base64.getEncoder().encode(os.toByteArray())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    private ShoeDetail updateShoeDetailProperties(ShoeDetailRequestUpdate requestShoeDetail) {
        Optional<Shoe> optionalShoe = shoeRepository.findById(requestShoeDetail.getShoe().getId());
        Optional<Color> optionalColor = colorRepository.findById(requestShoeDetail.getColor().getId());
        Optional<Category> optionalCategory = categoryRepository.findById(requestShoeDetail.getCategory().getId());
        Optional<Brand> optionalBrand = brandCategory.findById(requestShoeDetail.getBrand().getId());
        Optional<Size> optionalSize = sizeRepository.findById(requestShoeDetail.getSize().getId());
        Optional<Sole> optionalSole = soleRepository.findById(requestShoeDetail.getSole().getId());

        List<String> errors = new ArrayList<>();

        if (!optionalShoe.isPresent()) {
            errors.add("Shoe không tồn tại");
        }

        if (!optionalSize.isPresent()) {
            errors.add("Size không tồn tại");
        }

        if (!optionalSole.isPresent()) {
            errors.add("Sole không tồn tại");
        }

        if (!optionalBrand.isPresent()) {
            errors.add("Brand không tồn tại");
        }

        if (!optionalCategory.isPresent()) {
            errors.add("Loại giày không tồn tại");
        }

        if (!optionalColor.isPresent()) {
            errors.add("Màu sắc không tồn tại");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(", ", errors));
        }

        ShoeDetail shoeDetail = new ShoeDetail();
        shoeDetail.setShoe(optionalShoe.get());
        shoeDetail.setColor(optionalColor.get());
        shoeDetail.setCategory(optionalCategory.get());
        shoeDetail.setBrand(optionalBrand.get());
        shoeDetail.setSize(optionalSize.get());
        shoeDetail.setSole(optionalSole.get());
        shoeDetail.setPriceInput(requestShoeDetail.getPriceInput());
        shoeDetail.setQuantity(requestShoeDetail.getQuantity());
        shoeDetail.setCreatedAt(new Date());
        shoeDetail.setUpdatedAt(new Date());
        shoeDetail.setStatus(1);
        shoeDetail.setQrCode(requestShoeDetail.getQrCode());
        shoeDetail.setCreatedBy(requestShoeDetail.getCreatedBy());
        shoeDetail.setUpdatedBy(requestShoeDetail.getUpdatedBy());

        Float sizeName = optionalSize.get().getName();
        String shoeName = optionalShoe.get().getName();
        String colorName = optionalColor.get().getName();
        shoeDetail.setCode(shoeName.toLowerCase() + " - " + colorName.toLowerCase() + " - " + sizeName);
        return shoeDetailRepository.save(shoeDetail);
    }

    private ShoeDetail createShoeDetail(ShoeDetail requestShoeDetail) {
        Optional<Shoe> optionalShoe = shoeRepository.findById(requestShoeDetail.getShoe().getId());
        Optional<Color> optionalColor = colorRepository.findById(requestShoeDetail.getColor().getId());
        Optional<Category> optionalCategory = categoryRepository.findById(requestShoeDetail.getCategory().getId());
        Optional<Brand> optionalBrand = brandCategory.findById(requestShoeDetail.getBrand().getId());
        Optional<Size> optionalSize = sizeRepository.findById(requestShoeDetail.getSize().getId());
        Optional<Sole> optionalSole = soleRepository.findById(requestShoeDetail.getSole().getId());

        List<String> errors = new ArrayList<>();

        if (!optionalShoe.isPresent()) {
            errors.add("Shoe không tồn tại");
        }

        if (!optionalSize.isPresent()) {
            errors.add("Size không tồn tại");
        }

        if (!optionalSole.isPresent()) {
            errors.add("Sole không tồn tại");
        }

        if (!optionalBrand.isPresent()) {
            errors.add("Brand không tồn tại");
        }

        if (!optionalCategory.isPresent()) {
            errors.add("Loại giày không tồn tại");
        }

        if (!optionalColor.isPresent()) {
            errors.add("Màu sắc không tồn tại");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(", ", errors));
        }

        ShoeDetail shoeDetail = new ShoeDetail();
        shoeDetail.setShoe(optionalShoe.get());
        shoeDetail.setColor(optionalColor.get());
        shoeDetail.setCategory(optionalCategory.get());
        shoeDetail.setBrand(optionalBrand.get());
        shoeDetail.setSize(optionalSize.get());
        shoeDetail.setSole(optionalSole.get());
        shoeDetail.setPriceInput(requestShoeDetail.getPriceInput());
        shoeDetail.setQuantity(requestShoeDetail.getQuantity());
        shoeDetail.setCreatedAt(new Date());
        shoeDetail.setUpdatedAt(new Date());
        shoeDetail.setStatus(1);
        Float sizeName = optionalSize.get().getName();
        String shoeName = optionalShoe.get().getName();
        String colorName = optionalColor.get().getName();
        shoeDetail.setCode(shoeName.toLowerCase() + " - " + colorName.toLowerCase() + " - " + sizeName);
        return shoeDetailRepository.save(shoeDetail);
    }

    private void saveThumbnails(ShoeDetail shoeDetail, List<Thumbnail> thumbnails) {
        thumbnailRepository.deleteByShoeDetail_Id(shoeDetail.getId());
        for (Thumbnail thumbnail : thumbnails) {
            try {
                String thumbnailUrl = imageUploadService.uploadImageByName(String.valueOf(thumbnail.getImgName()));
                thumbnail.setImgName(thumbnail.getImgName());
                thumbnail.setImgUrl(thumbnailUrl);
                thumbnail.setShoeDetail(shoeDetail);
                thumbnailRepository.save(thumbnail);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading thumbnail");
            }
        }
    }

    private void saveImages(ShoeDetail shoeDetail, List<Image> images) {
        imageRepository.deleteByShoeDetail_Id(shoeDetail.getId());
        for (Image image : images) {
            try {
                String thumbnailUrl = imageUploadService.uploadImageByName(String.valueOf(image.getImgName()));
                image.setImgName(image.getImgName());
                image.setImgUrl(thumbnailUrl);
                image.setShoeDetail(shoeDetail);
                imageRepository.save(image);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image");
            }
        }
    }


    private String validateShoeDetail(ShoeDetailRequest shoeDetailRequest) {
        List<String> errorMessages = new ArrayList<>();
        for (ShoeDetail requestShoeDetail : shoeDetailRequest.getShoeDetailList()) {
            if (requestShoeDetail.getPriceInput() == null) {
                errorMessages.add("Thuộc tính không được để trống");
            }
        }
        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    private String validateUpdateShoeDetail(ShoeDetailRequestUpdate shoeDetailRequest) {
        List<String> errorMessages = new ArrayList<>();
        if (shoeDetailRequest.getPriceInput() == null) {
            errorMessages.add("Thuộc tính không được để trống");
        }
        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }

    // import
    @Override
    public byte[] createExcelFile() throws IOException {
        // Đường dẫn tới tệp Excel mẫu
        String excelResourcePath = "/static/fileMau/fileMauProduct.xlsx";

        // Đọc tệp Excel mẫu từ tài nguyên
        Resource resource = new ClassPathResource(excelResourcePath);
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
        // shoe
        // Lấy danh sách shoe
        List<Shoe> shoeList = shoeRepository.findAll();
        // Tạo sheet riêng cho shoeSheet
        Sheet shoeSheet = workbook.createSheet("ShoeSheet");
        // Đổ dữ liệu shoeSheet vào sheet ShoeSheet
        int rowIndexShoe = 0;
        for (int j = 0; j < shoeList.size(); j++) {
            Row row = shoeSheet.createRow(rowIndexShoe++);
            row.createCell(0).setCellValue(shoeList.get(j).getId() + " - " + shoeList.get(j).getName());
        }

        //brand
        List<Brand> brandList = brandCategory.findAll();
        Sheet brandSheet = workbook.createSheet("BrandSheet");
        int rowIndexBrand = 0;
        for (int k = 0; k < brandList.size(); k++) {
            Row row = brandSheet.createRow(rowIndexBrand++);
            row.createCell(0).setCellValue(brandList.get(k).getId() + " - " + brandList.get(k).getName());
        }

        //category
        List<Category> categoryList = categoryRepository.findAll();
        Sheet categorySheet = workbook.createSheet("CategorySheet");
        int rowIndexCategory = 0;
        for (int k = 0; k < categoryList.size(); k++) {
            Row row = categorySheet.createRow(rowIndexCategory++);
            row.createCell(0).setCellValue(categoryList.get(k).getId() + " - " + categoryList.get(k).getName());
        }
        // sole
        List<Sole> soleList = soleRepository.findAll();
        Sheet soleSheet = workbook.createSheet("SoleSheet");
        int rowIndexSole = 0;
        for (int k = 0; k < soleList.size(); k++) {
            Row row = soleSheet.createRow(rowIndexSole++);
            row.createCell(0).setCellValue(soleList.get(k).getId() + " - " + soleList.get(k).getName());
        }

        Sheet sheet = workbook.getSheet("Sheet1");
        int maxRows = 1000;
        for (int i = 1; i <= maxRows; i++) {
            Row currentRow = sheet.createRow(i);
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            // Ràng buộc dữ liệu cho hình thức giảm(Cột B) sử dụng giá trị từ shoe
            CellRangeAddressList addressListShoe = new CellRangeAddressList(i, i, 1, 1);
            DataValidationConstraint dvConstraintShoe = dvHelper.createFormulaListConstraint("ShoeSheet!$A$1:$A$" + rowIndexShoe);
            DataValidation validationShoe = dvHelper.createValidation(dvConstraintShoe, addressListShoe);
            validationShoe.setShowErrorBox(true);
            validationShoe.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationShoe.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Tên Giày.");
            sheet.addValidationData(validationShoe);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột C) sử dụng giá trị từ brand
            CellRangeAddressList addressListBrand = new CellRangeAddressList(i, i, 2, 2);
            DataValidationConstraint dvConstraintBrand = dvHelper.createFormulaListConstraint("BrandSheet!$A$1:$A$" + rowIndexBrand);
            DataValidation validationBrand = dvHelper.createValidation(dvConstraintBrand, addressListBrand);
            validationBrand.setShowErrorBox(true);
            validationBrand.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationBrand.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Hãng Giày.");
            sheet.addValidationData(validationBrand);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột D) sử dụng giá trị từ category
            CellRangeAddressList addressListCategory = new CellRangeAddressList(i, i, 3, 3);
            DataValidationConstraint dvConstraintCategory = dvHelper.createFormulaListConstraint("CategorySheet!$A$1:$A$" + rowIndexBrand);
            DataValidation validationCategory = dvHelper.createValidation(dvConstraintCategory, addressListCategory);
            validationCategory.setShowErrorBox(true);
            validationCategory.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationCategory.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Loại Giày.");
            sheet.addValidationData(validationCategory);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột E) sử dụng giá trị từ sole
            CellRangeAddressList addressListSole = new CellRangeAddressList(i, i, 4, 4);
            DataValidationConstraint dvConstraintSole = dvHelper.createFormulaListConstraint("SoleSheet!$A$1:$A$" + rowIndexBrand);
            DataValidation validationSole = dvHelper.createValidation(dvConstraintSole, addressListSole);
            validationSole.setShowErrorBox(true);
            validationSole.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationSole.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Đế Giày.");
            sheet.addValidationData(validationSole);
        }
        workbook.setSheetHidden(workbook.getSheetIndex("ShoeSheet"), true);
        workbook.setSheetHidden(workbook.getSheetIndex("BrandSheet"), true);
        workbook.setSheetHidden(workbook.getSheetIndex("CategorySheet"), true);
        workbook.setSheetHidden(workbook.getSheetIndex("SoleSheet"), true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    @Override
    public ShoeAndShoeDetailResponse createShoeAndShoeDetailResponse(Row row, List<String> errors, List<ShoeAndShoeDetailResponse> shoeAndShoeDetailListError, Integer type) {
        Cell nameShoeCell = row.getCell(1); // tenGiay
        Cell nameBrandCell = row.getCell(2); // hangGiay
        Cell nameCategoryCell = row.getCell(3); // loaiGiay
        Cell nameSoleCell = row.getCell(4); // deGiay
        Cell nameColorCell = row.getCell(5); // macSacGiay
        Cell nameSizeCell = row.getCell(6); // sizeGiay
        Cell priceCell = row.getCell(7); // gia
        Cell quantityCell = row.getCell(8); // soLuong

        String nameShoe = (nameShoeCell == null) ? " " : nameShoeCell.getStringCellValue();
        String nameBrand = (nameBrandCell == null) ? " " : nameBrandCell.getStringCellValue();
        String nameCategory = (nameCategoryCell == null) ? " " : nameCategoryCell.getStringCellValue();
        String nameSole = (nameSoleCell == null) ? " " : nameSoleCell.getStringCellValue();
        String nameColor;
        if (nameColorCell == null) {
            nameColor = " ";
        } else {
            if (nameColorCell.getCellType() == CellType.STRING) {
                nameColor = nameColorCell.getStringCellValue();
            } else if (nameColorCell.getCellType() == CellType.NUMERIC) {
                DataFormatter dataFormatter = new DataFormatter();
                nameColor = dataFormatter.formatCellValue(nameColorCell);
            } else {
                nameColor = " ";
            }
        }
        String nameSize;
        if (nameSizeCell == null) {
            nameSize = " ";
        } else {
            if (nameSizeCell.getCellType() == CellType.STRING) {
                nameSize = nameSizeCell.getStringCellValue();
            } else if (nameSizeCell.getCellType() == CellType.NUMERIC) {
                DataFormatter dataFormatter = new DataFormatter();
                nameSize = dataFormatter.formatCellValue(nameSizeCell);
            } else {
                nameSize = " ";
            }
        }
        //
        double price;
        if (priceCell == null || priceCell.getCellType() == CellType.BLANK) {
            price = 0.1;
        } else {
            price = priceCell.getNumericCellValue();
        }
        double quantity;
        if (quantityCell == null || quantityCell.getCellType() == CellType.BLANK) {
            quantity = 99999999;
        } else {
            quantity = quantityCell.getNumericCellValue();
        }
        //
        List<Shoe> shoeList = shoeRepository.findAll();
        List<Brand> brandList = brandCategory.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        List<Sole> soleList = soleRepository.findAll();
        List<Color> colorList = colorRepository.findAll();
        List<Size> sizeList = sizeRepository.findAll();
        List<Long> listColorImport = new ArrayList<>();
        List<Long> listSizeImport = new ArrayList<>();
        //
        ShoeAndShoeDetailResponse shoeAndShoeDetailResponse = new ShoeAndShoeDetailResponse();
        shoeAndShoeDetailResponse.setPrice(BigDecimal.valueOf(price));
        shoeAndShoeDetailResponse.setQuantity(Integer.valueOf((int) quantity));
        for (Shoe shoe : shoeList) {
            if (nameShoe.equals(shoe.getId() + " - " + shoe.getName())) {
                shoeAndShoeDetailResponse.setIdShoe(shoe.getId());
                break;
            }
        }
        shoeAndShoeDetailResponse.setNameShoe(nameShoe);
        shoeAndShoeDetailResponse.setNameBrand(nameBrand);
        for (Brand brand : brandList) {
            if (nameBrand.equals(brand.getId() + " - " + brand.getName())) {
                shoeAndShoeDetailResponse.setIdBrand(brand.getId());
                break;
            }
        }
        shoeAndShoeDetailResponse.setNameCategory(nameCategory);
        for (Category category : categoryList) {
            if (nameCategory.equals(category.getId() + " - " + category.getName())) {
                shoeAndShoeDetailResponse.setIdCategory(category.getId());
                break;
            }
        }
        shoeAndShoeDetailResponse.setNameSole(nameSole);
        for (Sole sole : soleList) {
            if (nameSole.equals(sole.getId() + " - " + sole.getName())) {
                shoeAndShoeDetailResponse.setIdSole(sole.getId());
                break;
            }
        }
        shoeAndShoeDetailResponse.setNameColor(nameColor);
        shoeAndShoeDetailResponse.setNameSize(nameSize);
        // checkvalidate import
        String errorMessage = "";
        if (nameShoe == null || nameShoeCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Tên giày không được để trống. " + "\n";
        }
        if (nameBrand == null || nameBrandCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Hãng giày không được để trống. " + "\n";
        }
        if (nameCategory == null || nameCategoryCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Loại giày không được để trống. " + "\n";
        }
        if (nameSole == null || nameSoleCell.getStringCellValue().trim().isEmpty()) {
            errorMessage += "Đế giày không được để trống. " + "\n";
        }
        if (nameColor == null) {
            errorMessage += "Màu sắc không được để trống. " + "\n";
        }
        if (nameSize == null) {
            errorMessage += "Size giày không được để trống. " + "\n";
        }
        if (!nameShoe.equals(" ") && nameShoe.trim().length() > 250) {
            errorMessage += "Tên giày không được lớn hơn 250. " + "\n";
        }
        if (!nameBrand.equals(" ") && nameBrand.trim().length() > 250) {
            errorMessage += "Hãng giày không được lớn hơn 250. " + "\n";
        }
        if (!nameCategory.equals(" ") && nameCategory.trim().length() > 250) {
            errorMessage += "Loại giày không được lớn hơn 250. " + "\n";
        }
        if (!nameSole.equals(" ") && nameSole.trim().length() > 250) {
            errorMessage += "Đế giày không được lớn hơn 250. " + "\n";
        }
        if (!nameColor.equals(" ") && nameColor.trim().length() > 250) {
            errorMessage += "Màu sắc giày không được lớn hơn 250. " + "\n";
        }
        if (!nameColor.equals(" ")) {
            if (nameColor.trim().matches("^[a-zA-Z0-9]+$")) {
                if (!colorList.stream().anyMatch(s -> String.valueOf(s.getId()).equalsIgnoreCase(nameColor.trim()))) {
                    errorMessage += "Màu sắc " + nameColor + " không tồn tại " + "\n";
                } else {
                    listColorImport.add(Long.valueOf(nameColor));
                    shoeAndShoeDetailResponse.setIdColor(listColorImport);
                }
            } else {
                if (!nameColor.trim().matches("^(\\w+(,\\s*)?)*$")) {
                    errorMessage += "Màu sắc sai định dạng! " + "\n";
                } else {
                    String[] colorImport = nameColor.toUpperCase().trim().split(",");
                    for (int i = 0; i < colorImport.length; i++) {
                        boolean found = false;
                        for (int j = 0; j < colorList.size(); j++) {
                            if (colorImport[i].trim().equals(String.valueOf(colorList.get(j).getId()))) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            errorMessage += "Màu sắc" + colorImport[i].trim() + " không tồn tại " + "\n";
                        } else {
                            for (int a = 0; a < colorList.size(); a++) {
                                for (int b = 0; b < colorImport.length; b++) {
                                    if (colorImport[b].equals(String.valueOf(colorList.get(a).getId()))) {
                                        Long colorId = colorList.get(a).getId();
                                        if (!listColorImport.isEmpty()) {
                                            for (int k = 0; k < listColorImport.size(); k++) {
                                                if (colorId == listColorImport.get(k)) {
                                                    break;
                                                } else {
                                                    listColorImport.add(colorId);
                                                }
                                            }
                                        } else {
                                            listColorImport.add(colorId);
                                        }
                                    }
                                }
                            }
                            shoeAndShoeDetailResponse.setIdColor(listColorImport);
                        }
                    }
                }
            }
        }
        if (!nameSize.equals(" ") && nameSize.trim().length() > 250) {
            errorMessage += "Size giày không được lớn hơn 250. " + "\n";
        }
        if (!nameSize.equals(" ")) {
            if (nameSize.trim().matches("^[a-zA-Z0-9]+$")) {
                if (!sizeList.stream().anyMatch(s -> String.valueOf(s.getId()).equalsIgnoreCase(nameSize.trim()))) {
                    errorMessage += "Size giày " + nameSize + " không tồn tại " + "\n";
                } else {
                    listSizeImport.add(Long.valueOf(nameSize));
                    shoeAndShoeDetailResponse.setIdSize(listSizeImport);
                }
            } else {
                if (!nameSize.trim().matches("^(\\w+(,\\s*)?)*$")) {
                    errorMessage += "Size giày sai định dạng! " + "\n";
                } else {
                    String[] sizeImport = nameSize.toUpperCase().trim().split(",");
                    for (int i = 0; i < sizeImport.length; i++) {
                        boolean found = false;
                        for (int j = 0; j < sizeList.size(); j++) {
                            if (sizeImport[i].trim().equals(String.valueOf(sizeList.get(j).getId()))) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            errorMessage += "Size giày " + sizeImport[i].trim() + " không tồn tại " + "\n";
                        } else {
                            for (int a = 0; a < sizeList.size(); a++) {
                                for (int j = 0; j < sizeImport.length; j++) {
                                    if (sizeImport[j].equals(String.valueOf(sizeList.get(a).getId()))) {
                                        Long sizeId = sizeList.get(a).getId();
                                        if (!listSizeImport.isEmpty()) {
                                            for (int k = 0; k < listSizeImport.size(); k++) {
                                                if (sizeId == listSizeImport.get(k)) {
                                                    break;
                                                } else {
                                                    listSizeImport.add(sizeId);
                                                }
                                            }
                                        } else {
                                            listSizeImport.add(sizeId);
                                        }
                                    }
                                }
                            }
                            shoeAndShoeDetailResponse.setIdSize(listSizeImport);
                        }
                    }
                }
            }
        }
        if (priceCell == null || priceCell.getCellType() == CellType.BLANK) {
            errorMessage += "Giá của giày không được để trống. " + "\n";
        }
        if (priceCell != null) {
            if (price == 0) {
                errorMessage += "Giá của giày phải lớn hơn 0 " + "\n";
            }
        }
        if (quantityCell == null || quantityCell.getCellType() == CellType.BLANK) {
            errorMessage += "Số lượng không được để trống. " + "\n";
        }
        if (quantityCell != null) {
            if (quantity == 0) {
                errorMessage += "Số lượng phải lớn hơn 0 " + "\n";
            }
        }
        if (!listColorImport.isEmpty() && !listSizeImport.isEmpty()) {
            for (int i = 0; i < shoeAndShoeDetailResponse.getIdColor().size(); i++) {
                for (int j = 0; j < shoeAndShoeDetailResponse.getIdSize().size(); j++) {
                    Shoe shoe = shoeRepository.findById(shoeAndShoeDetailResponse.getIdShoe()).get();
                    Brand brand = brandCategory.findById(shoeAndShoeDetailResponse.getIdBrand()).get();
                    Category category = categoryRepository.findById(shoeAndShoeDetailResponse.getIdCategory()).get();
                    Sole sole = soleRepository.findById(shoeAndShoeDetailResponse.getIdSole()).get();
                    Color color = colorRepository.findById(shoeAndShoeDetailResponse.getIdColor().get(i)).get();
                    Size size = sizeRepository.findById(shoeAndShoeDetailResponse.getIdSize().get(j)).get();
                    if (type == 1) {
                        ShoeDetail shoeDetailUpdate = shoeDetailRepository.getOneByAllForeignkey(shoe.getId(), size.getId(), sole.getId(), brand.getId(), category.getId(), color.getId());
                        if (shoeDetailUpdate == null) {
                            errorMessage += "Giày không tồn tại. " + "\n";
                        }
                    } else {
                        ShoeDetail shoeDetailUpdate = shoeDetailRepository.getOneByAllForeignkey(shoe.getId(), size.getId(), sole.getId(), brand.getId(), category.getId(), color.getId());
                        if (shoeDetailUpdate != null) {
                            errorMessage += "Giày đã tồn tại. " + "\n";
                        }
                    }
                }
            }
        }
        if (!errorMessage.isEmpty()) {
            errors.add(errorMessage);
            shoeAndShoeDetailListError.add(shoeAndShoeDetailResponse);
            return null;
        } else {
            // Nếu không có lỗi, trả về đối tượng shoeAndShoeDetailResponse
            return shoeAndShoeDetailResponse;
        }
    }

    @Override
    public void saveShoeAndShoeDetail(ShoeAndShoeDetailResponse shoeAndShoeDetailResponse, Integer type) {
        Date date = new Date();
        for (int i = 0; i < shoeAndShoeDetailResponse.getIdColor().size(); i++) {
            for (int j = 0; j < shoeAndShoeDetailResponse.getIdSize().size(); j++) {
                Shoe shoe = shoeRepository.findById(shoeAndShoeDetailResponse.getIdShoe()).get();
                Brand brand = brandCategory.findById(shoeAndShoeDetailResponse.getIdBrand()).get();
                Category category = categoryRepository.findById(shoeAndShoeDetailResponse.getIdCategory()).get();
                Sole sole = soleRepository.findById(shoeAndShoeDetailResponse.getIdSole()).get();
                Color color = colorRepository.findById(shoeAndShoeDetailResponse.getIdColor().get(i)).get();
                Size size = sizeRepository.findById(shoeAndShoeDetailResponse.getIdSize().get(j)).get();
                if (type == 1) {
                    ShoeDetail shoeDetailUpdate = shoeDetailRepository.getOneByAllForeignkey(shoe.getId(), size.getId(), sole.getId(), brand.getId(), category.getId(), color.getId());
                    shoeDetailUpdate.setUpdatedAt(date);
                    shoeDetailUpdate.setQuantity(shoeDetailUpdate.getQuantity() + shoeAndShoeDetailResponse.getQuantity());
                    shoeDetailUpdate.setPriceInput(shoeAndShoeDetailResponse.getPrice());
                    shoeDetailRepository.save(shoeDetailUpdate);
                } else {
                    ShoeDetail shoeDetail = new ShoeDetail();
                    shoeDetail.setShoe(shoe);
                    shoeDetail.setBrand(brand);
                    shoeDetail.setCategory(category);
                    shoeDetail.setSole(sole);
                    shoeDetail.setColor(color);
                    shoeDetail.setSize(size);
                    shoeDetail.setPriceInput(shoeAndShoeDetailResponse.getPrice());
                    shoeDetail.setQuantity(shoeAndShoeDetailResponse.getQuantity());
                    shoeDetail.setCreatedAt(date);
                    shoeDetail.setStatus(1);
                    shoeDetail.setCode(shoe.getName().toLowerCase() + " - " + color.getName().toLowerCase() + " - " + size.getName());
                    shoeDetailRepository.save(shoeDetail);
                    String qrCode = generateQrCode(ShoeDetail.builder().id(shoeDetail.getId()).build());
                    saveQrCode(shoeDetail, qrCode);
                }
            }
        }
    }

    @Override
    public ResponseImport importDataFromExcel(MultipartFile file, Integer type) throws IOException {
        int fail = 0;
        int total = 0;
        List<String> errors = new ArrayList<>();
        List<ShoeAndShoeDetailResponse> shoeAndShoeDetailResponsesListError = new ArrayList<>();
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
                ShoeAndShoeDetailResponse shoeAndShoeDetailResponse = createShoeAndShoeDetailResponse(row, errors, shoeAndShoeDetailResponsesListError, type);

                if (shoeAndShoeDetailResponse != null) {
                    // Xử lý dữ liệu hợp lệ và lưu voucherRespose vào database
                    saveShoeAndShoeDetail(shoeAndShoeDetailResponse, type);
                }
                total++;
            }
        }
        if (!errors.isEmpty()) {
            String existingExcelFilePath = "src/main/java/com/backend/file/error-file-product.xlsx";
            createErrorExcelFile(existingExcelFilePath, errors, shoeAndShoeDetailResponsesListError, type);
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

    public void createErrorExcelFile(String existingExcelFilePath, List<String> errors, List<ShoeAndShoeDetailResponse> shoeAndShoeDetailListError, Integer type) {
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
            List<Shoe> shoeList = shoeRepository.findAll();
            List<Brand> brandList = brandCategory.findAll();
            List<Category> categoryList = categoryRepository.findAll();
            List<Sole> soleList = soleRepository.findAll();
            List<Color> colorList = colorRepository.findAll();
            List<Size> sizeList = sizeRepository.findAll();

            Cell headerCell = headerRow.createCell(9);
            headerCell.setCellValue("Chi tiết lỗi");
            int columnWidth = 40;
            errorSheet.setColumnWidth(9, columnWidth * 256);
            CellStyle headerCellStyle = createHeaderCellStyle(errorWorkbook);
            headerCell.setCellStyle(headerCellStyle);


            // Font cho lỗi
            CellStyle errorCellStyle = createErrorCellStyle(errorWorkbook);

            // Font cho symble
            CellStyle cellSymble = createErrorCellStyleSymble(errorWorkbook);

            // Border cho lỗi
            CellStyle cellStyle = createBorderCellStyle(errorWorkbook);

            if (errorSheet == null) {
                errorSheet = errorWorkbook.createSheet("Sheet1");
            }
            LocalDateTime currentDateTime = LocalDateTime.now();
            int rowIndex = 1;
            for (int i = 0; i < errors.size() || i < shoeAndShoeDetailListError.size(); i++) {
                Row errorRow = errorSheet.createRow(rowIndex++);
                // Tạo ô dữ liệu từ danh sách errors nếu có
                if (i < errors.size()) {
                    Cell cell = errorRow.createCell(9);
                    cell.setCellValue(errors.get(i));
                    cell.setCellStyle(errorCellStyle);
                }

                // Tạo ô dữ liệu từ danh sách voucherListError nếu có
                if (i < shoeAndShoeDetailListError.size()) {
                    Cell cellSTT = errorRow.createCell(0);
                    cellSTT.setCellValue(i + 1);
                    cellSTT.setCellStyle(cellSymble);

                    // nameShoe
                    Cell cellShoeName = errorRow.createCell(1);
                    cellShoeName.setCellValue(shoeAndShoeDetailListError.get(i).getNameShoe().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameShoe().equals(" ") || shoeAndShoeDetailListError.get(i).getNameShoe().trim().isEmpty()) {
                        cellShoeName.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameShoe().trim().length() > 250) {
                        cellShoeName.setCellStyle(cellStyle);
                    } else {
                        cellShoeName.setCellStyle(cellSymble);
                    }
                    // brand
                    Cell cellBrandName = errorRow.createCell(2);
                    cellBrandName.setCellValue(shoeAndShoeDetailListError.get(i).getNameBrand().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameBrand().equals(" ") || shoeAndShoeDetailListError.get(i).getNameBrand().trim().isEmpty()) {
                        cellBrandName.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameBrand().trim().length() > 250) {
                        cellBrandName.setCellStyle(cellStyle);
                    } else {
                        cellBrandName.setCellStyle(cellSymble);
                    }
                    // category
                    Cell cellCategoryName = errorRow.createCell(3);
                    cellCategoryName.setCellValue(shoeAndShoeDetailListError.get(i).getNameCategory().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameCategory().equals(" ") || shoeAndShoeDetailListError.get(i).getNameCategory().trim().isEmpty()) {
                        cellCategoryName.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameCategory().trim().length() > 250) {
                        cellCategoryName.setCellStyle(cellStyle);
                    } else {
                        cellCategoryName.setCellStyle(cellSymble);
                    }
                    // sole
                    Cell cellSoleName = errorRow.createCell(4);
                    cellSoleName.setCellValue(shoeAndShoeDetailListError.get(i).getNameSole().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameSole().equals(" ") || shoeAndShoeDetailListError.get(i).getNameSole().trim().isEmpty()) {
                        cellSoleName.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameSole().trim().length() > 250) {
                        cellSoleName.setCellStyle(cellStyle);
                    } else {
                        cellSoleName.setCellStyle(cellSymble);
                    }
                    // color
                    Cell cellColorName = errorRow.createCell(5);
                    cellColorName.setCellValue(shoeAndShoeDetailListError.get(i).getNameColor().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameColor().equals(" ") || shoeAndShoeDetailListError.get(i).getNameColor().trim().isEmpty()) {
                        cellColorName.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameColor().trim().length() > 250) {
                        cellColorName.setCellStyle(cellStyle);
                    } else if (!shoeAndShoeDetailListError.get(i).getNameColor().equals(" ") || !shoeAndShoeDetailListError.get(i).getNameColor().trim().isEmpty()) {
                        if (shoeAndShoeDetailListError.get(i).getNameColor().trim().matches("^[a-zA-Z0-9]+$")) {
                            String nameColorCheck = shoeAndShoeDetailListError.get(i).getNameColor().trim();
                            if (!colorList.stream().anyMatch(s -> String.valueOf(s.getId()).equalsIgnoreCase(nameColorCheck.trim()))) {
                                cellColorName.setCellStyle(cellStyle);
                            }
                        } else {
                            cellColorName.setCellStyle(cellSymble);
                            String[] colorImport = shoeAndShoeDetailListError.get(i).getNameColor().toUpperCase().trim().split(",");
                            for (int a = 0; a < colorImport.length; a++) {
                                boolean found = false;
                                for (int j = 0; j < colorList.size(); j++) {
                                    if (colorImport[a].trim().equals(String.valueOf(colorList.get(j).getId()))) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    cellColorName.setCellStyle(cellStyle);
                                }
                            }
                        }
                    } else {
                        cellColorName.setCellStyle(cellSymble);
                    }
                    // size
                    Cell cellSize = errorRow.createCell(6);
                    cellSize.setCellValue(shoeAndShoeDetailListError.get(i).getNameSize().trim());
                    if (shoeAndShoeDetailListError.get(i).getNameSize().equals(" ") || shoeAndShoeDetailListError.get(i).getNameSize().trim().isEmpty()) {
                        cellSize.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getNameSize().trim().length() > 250) {
                        cellSize.setCellStyle(cellStyle);
                    } else if (!shoeAndShoeDetailListError.get(i).getNameSize().equals(" ") || !shoeAndShoeDetailListError.get(i).getNameSize().trim().isEmpty()) {
                        if (shoeAndShoeDetailListError.get(i).getNameSize().trim().matches("^[a-zA-Z0-9]+$")) {
                            String nameSizeCheck = shoeAndShoeDetailListError.get(i).getNameSize().trim();
                            if (!sizeList.stream().anyMatch(s -> String.valueOf(s.getId()).equalsIgnoreCase(nameSizeCheck.trim()))) {
                                cellSize.setCellStyle(cellStyle);
                            }
                        } else {
                            cellSize.setCellStyle(cellSymble);
                            String[] sizeImport = shoeAndShoeDetailListError.get(i).getNameSize().toUpperCase().trim().split(",");
                            for (int a = 0; a < sizeImport.length; a++) {
                                boolean found = false;
                                for (int j = 0; j < sizeList.size(); j++) {
                                    if (sizeImport[a].trim().equals(String.valueOf(colorList.get(j).getId()))) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    cellSize.setCellStyle(cellStyle);
                                }
                            }
                        }
                    } else {
                        cellSize.setCellStyle(cellSymble);
                    }
                    // price
                    Cell cellPrice = errorRow.createCell(7);
                    BigDecimal price = shoeAndShoeDetailListError.get(i).getPrice();
                    if (price.compareTo(new BigDecimal("0.1")) == 0) {
                        cellPrice.setCellValue((Date) null);
                        cellPrice.setCellStyle(cellStyle);
                    } else if (price.compareTo(BigDecimal.ZERO) == 0) {
                        cellPrice.setCellValue(0);
                        cellPrice.setCellStyle(cellStyle);
                    } else {
                        cellPrice.setCellValue(price.toString());
                        cellPrice.setCellStyle(cellSymble);
                    }
                    // quantity
                    Cell cellQuantity = errorRow.createCell(8);
                    if (shoeAndShoeDetailListError.get(i).getQuantity() == 99999999) {
                        cellQuantity.setCellValue((Date) null);
                        cellQuantity.setCellStyle(cellStyle);
                    } else if (shoeAndShoeDetailListError.get(i).getQuantity() == 0) {
                        cellQuantity.setCellValue(shoeAndShoeDetailListError.get(i).getQuantity());
                        cellQuantity.setCellStyle(cellStyle);
                    } else {
                        cellQuantity.setCellValue(shoeAndShoeDetailListError.get(i).getQuantity());
                        cellQuantity.setCellStyle(cellSymble);
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
        FileInputStream inputStream = new FileInputStream("src/main/java/com/backend/file/error-file-product.xlsx");
        // Đọc dữ liệu từ InputStream vào mảng byte
        byte[] excelBytes;
        try {
            excelBytes = inputStream.readAllBytes();
        } finally {
            inputStream.close();
        }
        return excelBytes;
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
}
