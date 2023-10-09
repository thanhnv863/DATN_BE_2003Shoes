package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ShoeDetailRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Override
    public ServiceResult<Shoe> resultValidate(String mess) {
        return new ServiceResult<>(AppConstant.FAIL, mess, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest) {
        String result = validateNhanVien(shoeDetailRequest);
        if (result != null) {
            return resultValidate(result);
        } else {
            try {
                for (ShoeDetail requestShoeDetail : shoeDetailRequest.getShoeDetailList()) {
                    ShoeDetail shoeDetail = createShoeDetail(requestShoeDetail);
                    saveThumbnails(shoeDetail, requestShoeDetail.getThumbnails());
                    saveImages(shoeDetail, requestShoeDetail.getImages());
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
        shoeDetail.setStatus(0);
        Float sizeName = optionalSize.get().getName();
        String shoeName = optionalShoe.get().getName();
        String colorName = optionalColor.get().getName();
        shoeDetail.setCode(shoeName.toLowerCase() + " - " + colorName.toLowerCase() + " - " + sizeName);
        return shoeDetailRepository.save(shoeDetail);
    }

    private void saveThumbnails(ShoeDetail shoeDetail, List<Thumbnail> thumbnails) {
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

    @Override
    public String validateNhanVien(ShoeDetailRequest shoeDetailRequest) {
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


}
