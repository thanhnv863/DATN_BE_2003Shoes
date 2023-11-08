package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.OrderReponse;
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
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
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
                searchShoeDetailRequest.getMaxPrice()
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
//            validationType.setShowErrorBox(true);
//            validationType.setErrorStyle(DataValidation.ErrorStyle.STOP);
//            validationType.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách hình thức giảm.");
            sheet.addValidationData(validationShoe);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột C) sử dụng giá trị từ brand
            CellRangeAddressList addressListBrand = new CellRangeAddressList(i, i, 2, 2);
            DataValidationConstraint dvConstraintBrand  = dvHelper.createFormulaListConstraint("BrandSheet!$A$1:$A$" + rowIndexBrand);
            DataValidation validationBrand  = dvHelper.createValidation(dvConstraintBrand, addressListBrand);
            validationBrand.setShowErrorBox(true);
            validationBrand.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationBrand.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Hãng Giày.");
            sheet.addValidationData(validationBrand);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột D) sử dụng giá trị từ category
            CellRangeAddressList addressListCategory = new CellRangeAddressList(i, i, 3, 3);
            DataValidationConstraint dvConstraintCategory  = dvHelper.createFormulaListConstraint("CategorySheet!$A$1:$A$" + rowIndexBrand);
            DataValidation validationCategory = dvHelper.createValidation(dvConstraintCategory, addressListCategory);
            validationCategory.setShowErrorBox(true);
            validationCategory.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validationCategory.createErrorBox("Lỗi dữ liệu", "Chọn một giá trị từ danh sách Loại Giày.");
            sheet.addValidationData(validationCategory);

            // Ràng buộc dữ liệu cho hình thức giảm(Cột E) sử dụng giá trị từ sole
            CellRangeAddressList addressListSole = new CellRangeAddressList(i, i, 4, 4);
            DataValidationConstraint dvConstraintSole  = dvHelper.createFormulaListConstraint("SoleSheet!$A$1:$A$" + rowIndexBrand);
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
}
