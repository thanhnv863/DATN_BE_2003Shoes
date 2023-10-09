package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ShoeRequest;
import com.backend.dto.response.ShoeResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.dto.response.shoedetail.Meta;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Image;
import com.backend.entity.Shoe;
import com.backend.entity.ShoeDetail;
import com.backend.repository.BrandRepository;
import com.backend.repository.CategoryRepository;
import com.backend.repository.ColorRepository;
import com.backend.repository.ImageRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.repository.ShoeRepository;
import com.backend.repository.SizeRepository;
import com.backend.repository.SoleRepository;
import com.backend.repository.ThumbnailRepository;
import com.backend.service.IShoeService;
import com.backend.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoeServiceImpl implements IShoeService {

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


//    @Override
//    public ServiceResult<Shoe> resultValidate(String mess) {
//        return new ServiceResult<>(AppConstant.FAIL, mess, null);
//    }


//    @Override
//    public ServiceResult<List<ResultItem>> getAllShoeItems(int page, int size) {
//        Page<Tuple> tuplePage = shoeDetailRepository.getAllShoeDetail(PageRequest.of(page, size));
//        System.out.println("tuplePage"+tuplePage);
//        List<ResultItem> result = new ArrayList<>();
//        result = tuplePage.stream()
//                .map(tuple -> {
//                    Long id = tuple.get(0, BigInteger.class).longValue();
//                    String shoeName = tuple.get(1, String.class);
//                    Float sizeName = tuple.get(2, Double.class).floatValue();
//                    String categoryName = tuple.get(3, String.class);
//                    String brandName = tuple.get(4, String.class);
//                    String soleName = tuple.get(5, String.class);
//                    String colorName = tuple.get(6, String.class);
//                    String codeName = tuple.get(7, String.class);
//                    BigDecimal priceInput = tuple.get(8, BigDecimal.class);
//                    Integer qty = tuple.get(9, Integer.class);
//                    String thumbnail = tuple.get(10, String.class);
//                    return new ResultItem(id,shoeName,sizeName,categoryName,brandName
//                            ,soleName,colorName,codeName,priceInput,qty,thumbnail);
//                }).collect(Collectors.toList());
//        return new ServiceResult<>(AppConstant.SUCCESS,
//                "Successfully retrieved",
//                result
//        );
//    }

    @Override
    public ServiceResult<List<DataPaginate>> getAllShoeItemstest(int page, int size, String nameShoe, Float sizeShoe, String brandShoe) {

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");

        Page<ShoeDetail> shoeDetails;

        if (nameShoe != null && sizeShoe != null && brandShoe != null) {
            shoeDetails = shoeDetailRepository.findByShoe_NameContainingAndSize_NameAndBrand_NameContaining(nameShoe, sizeShoe, brandShoe, PageRequest.of(page - 1, size, sort));
        } else if (nameShoe != null && sizeShoe != null) {
            shoeDetails = shoeDetailRepository.findByShoe_NameContainingAndSize_Name(nameShoe, sizeShoe, PageRequest.of(page - 1, size, sort));
        } else if (nameShoe != null && brandShoe != null) {
            shoeDetails = shoeDetailRepository.findByShoe_NameContainingAndBrand_NameContaining(nameShoe, brandShoe, PageRequest.of(page - 1, size, sort));
        } else if (sizeShoe != null && brandShoe != null) {
            shoeDetails = shoeDetailRepository.findBySize_NameAndBrand_NameContaining(sizeShoe, brandShoe, PageRequest.of(page - 1, size, sort));
        } else if (nameShoe != null) {
            shoeDetails = shoeDetailRepository.findByShoe_NameContaining(nameShoe, PageRequest.of(page - 1, size, sort));
        } else if (sizeShoe != null) {
            shoeDetails = shoeDetailRepository.findBySize_Name(sizeShoe, PageRequest.of(page - 1, size, sort));
        } else if (brandShoe != null) {
            shoeDetails = shoeDetailRepository.findByBrand_NameContaining(brandShoe, PageRequest.of(page - 1, size, sort));
        } else {
            shoeDetails = shoeDetailRepository.findAll(PageRequest.of(page - 1, size, sort));
        }

        int current = shoeDetails.getNumber();
        int pageSize = shoeDetails.getSize();
        int pages = shoeDetails.getTotalPages();
        long total = shoeDetails.getTotalElements();

        Meta meta = new Meta();
        meta.setCurrent(current);
        meta.setPageSize(pageSize);
        meta.setPages(pages);
        meta.setTotal(total);

        List<ResultItem> resultItems = new ArrayList<>();

        for (ShoeDetail shoeDetail : shoeDetails) {
            ResultItem resultItem = new ResultItem();
            resultItem.setId(shoeDetail.getId());
            resultItem.setNameShoe(shoeDetail.getShoe().getName());
            resultItem.setSize(shoeDetail.getSize().getName());
            resultItem.setCategory(shoeDetail.getCategory().getName());
            resultItem.setBrand(shoeDetail.getBrand().getName());
            resultItem.setSole(shoeDetail.getSole().getName());
            resultItem.setColor(shoeDetail.getColor().getName());
            resultItem.setCode(shoeDetail.getCode());
            resultItem.setQrCode(shoeDetail.getQrCode());
            resultItem.setPriceInput(shoeDetail.getPriceInput());
            resultItem.setQty(shoeDetail.getQuantity());
            resultItem.setCreatedAt(shoeDetail.getCreatedAt());
            resultItem.setUpdatedAt(shoeDetail.getUpdatedAt());
            resultItem.setThumbnail(shoeDetail.getThumbnails().get(0).getImgUrl());

            List<String> images = getImagesForShoeDetail(shoeDetail.getId());
            resultItem.setImages(images);

            resultItems.add(resultItem);
        }
        DataPaginate dataPaginate = new DataPaginate();
        dataPaginate.setMeta(meta);
        dataPaginate.setResult(resultItems);

        return new ServiceResult(AppConstant.SUCCESS,
                "Successfully retrieved",
                dataPaginate
        );
    }

    @Override
    public ServiceResult<List<ShoeResponse>> getAllShoeName() {
        List<Shoe> shoeList = shoeRepository.findAll();
        List<ShoeResponse> shoeResponseList = convertToReponse(shoeList);
        return new ServiceResult(AppConstant.SUCCESS, "Success", shoeResponseList);
    }

    @Override
    public ServiceResult<ShoeResponse> addNewShoeName(ShoeRequest shoeRequest) {
        Optional<Shoe> optionalShoe = shoeRepository.findByNameShoe(shoeRequest.getName());
        if (optionalShoe.isPresent()) {
            return new ServiceResult(AppConstant.FAIL, "Giày không tồn tại!", null);
        } else {
            Shoe shoe = new Shoe();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            shoe.setName(shoeRequest.getName());
            shoe.setStatus(0);
            shoe.setCreatedAt(date);
            shoe.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS, "Success", shoeRepository.save(shoe));
        }

    }

    private List<ShoeResponse> convertToReponse(List<Shoe> shoeList) {
        return shoeList.stream().map(shoe ->
                ShoeResponse.builder()
                        .id(shoe.getId())
                        .name(shoe.getName())
                        .createdAt(shoe.getCreatedAt())
                        .updatedAt(shoe.getUpdatedAt())
                        .status(shoe.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<String> getImagesForShoeDetail(Long shoeDetailId) {
        List<Image> images = imageRepository.findByShoeDetailId(shoeDetailId);

        List<String> imageUrls = images.stream()
                .map(Image::getImgUrl)
                .collect(Collectors.toList());

        return imageUrls;
    }

//    private Shoe createShoe(ShoeRequest shoeRequest) {
//        Shoe shoe = new Shoe();
//        Calendar calendar = Calendar.getInstance();
//        Date date = calendar.getTime();
//        shoe.setName(shoeRequest.getName());
//        shoe.setCreatedAt(date);
//        shoe.setUpdatedAt(date);
//        shoe.setStatus(shoeRequest.getStatusShoe());
//        return shoeRepository.save(shoe);
//    }

//    @Override
//    public String validateNhanVien(ShoeRequest shoeRequest) {
//        List<String> errorMessages = new ArrayList<>();
//        if (shoeRequest.getName() == null || shoeRequest.getStatusShoe() == null) {
//            errorMessages.add("Thông tin giày không được để trống");
//        }
//        for (ShoeDetail requestShoeDetail : shoeRequest.getShoeDetailList()) {
//            if (requestShoeDetail.getPriceInput() == null) {
//                errorMessages.add("Thuộc tính không được để trống");
//            }
//        }
//        if (errorMessages.size() > 0) {
//            return String.join(", ", errorMessages);
//        } else {
//            return null;
//        }
//
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ServiceResult<Shoe> addNewShoe(ShoeRequest shoeRequest) {
//        String result = validateNhanVien(shoeRequest);
//        if (result != null) {
//            return resultValidate(result);
//        } else {
//            try {
//                for (ShoeDetail requestShoeDetail : shoeRequest.getShoeDetailList()) {
//                    ShoeDetail shoeDetail = createShoeDetail(requestShoeDetail);
//                    saveThumbnails(shoeDetail, requestShoeDetail.getThumbnails());
//                    saveImages(shoeDetail, requestShoeDetail.getImages());
//                    String qrCode = generateQrCode(ShoeDetail.builder().id(shoeDetail.getId()).build());
//                    saveQrCode(shoeDetail, qrCode);
//                }
//                return new ServiceResult<>(AppConstant.SUCCESS, "Shoe added successfully", null);
//            } catch (Exception e) {
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
//            }
//        }
//
//    }
//
//    private void saveQrCode(ShoeDetail shoeDetail, String qrCode) {
//        shoeDetail.setQrCode(qrCode);
//        shoeDetailRepository.save(shoeDetail);
//    }
//
//    private String generateQrCode(ShoeDetail shoeDetail) {
//        String prettyData = prettyObj(shoeDetail);
//        String qrCode = processingGenerateQrCode(prettyData, 300, 300);
//        return qrCode;
//    }
//
//    private String prettyObj(Object obj) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    private String processingGenerateQrCode(String data, int width, int height) {
//        StringBuilder result = new StringBuilder();
//        if (!data.isEmpty()) {
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            try {
//                QRCodeWriter writer = new QRCodeWriter();
//                BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
//
//                BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//                ImageIO.write(bufferedImage, "png", os);
//
//                result.append("data:image/png;base64,");
//                result.append(new String(Base64.getEncoder().encode(os.toByteArray())));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return result.toString();
//    }
//
//    private ShoeDetail createShoeDetail(ShoeDetail requestShoeDetail) {
//        Optional<Shoe> optionalShoe = shoeRepository.findById(requestShoeDetail.getShoe().getId());
//        Optional<Color> optionalColor = colorRepository.findById(requestShoeDetail.getColor().getId());
//        Optional<Category> optionalCategory = categoryRepository.findById(requestShoeDetail.getCategory().getId());
//        Optional<Brand> optionalBrand = brandCategory.findById(requestShoeDetail.getBrand().getId());
//        Optional<Size> optionalSize = sizeRepository.findById(requestShoeDetail.getSize().getId());
//        Optional<Sole> optionalSole = soleRepository.findById(requestShoeDetail.getSole().getId());
//
//        List<String> errors = new ArrayList<>();
//
//        if (!optionalShoe.isPresent()) {
//            errors.add("Shoe không tồn tại");
//        }
//
//        if (!optionalSize.isPresent()) {
//            errors.add("Size không tồn tại");
//        }
//
//        if (!optionalSole.isPresent()) {
//            errors.add("Sole không tồn tại");
//        }
//
//        if (!optionalBrand.isPresent()) {
//            errors.add("Brand không tồn tại");
//        }
//
//        if (!optionalCategory.isPresent()) {
//            errors.add("Loại giày không tồn tại");
//        }
//
//        if (!optionalColor.isPresent()) {
//            errors.add("Màu sắc không tồn tại");
//        }
//
//        if (!errors.isEmpty()) {
//            throw new RuntimeException(String.join(", ", errors));
//        }
//
//        ShoeDetail shoeDetail = new ShoeDetail();
//        shoeDetail.setShoe(optionalShoe.get());
//        shoeDetail.setColor(optionalColor.get());
//        shoeDetail.setCategory(optionalCategory.get());
//        shoeDetail.setBrand(optionalBrand.get());
//        shoeDetail.setSize(optionalSize.get());
//        shoeDetail.setSole(optionalSole.get());
//        shoeDetail.setPriceInput(requestShoeDetail.getPriceInput());
//        shoeDetail.setQuantity(requestShoeDetail.getQuantity());
//        shoeDetail.setCreatedAt(new Date());
//        shoeDetail.setUpdatedAt(new Date());
//        shoeDetail.setStatus(0);
//        Float sizeName = optionalSize.get().getName();
//        String shoeName = optionalShoe.get().getName();
//        String colorName = optionalColor.get().getName();
//        shoeDetail.setCode(shoeName.toLowerCase() + " - " + colorName.toLowerCase() + " - " + sizeName);
//        return shoeDetailRepository.save(shoeDetail);
//    }
//
//    private void saveThumbnails(ShoeDetail shoeDetail, List<Thumbnail> thumbnails) {
//        for (Thumbnail thumbnail : thumbnails) {
//            try {
//                String thumbnailUrl = imageUploadService.uploadImageByName(String.valueOf(thumbnail.getImgName()));
//                thumbnail.setImgName(thumbnail.getImgName());
//                thumbnail.setImgUrl(thumbnailUrl);
//                thumbnail.setShoeDetail(shoeDetail);
//                thumbnailRepository.save(thumbnail);
//            } catch (IOException e) {
//                throw new RuntimeException("Error uploading thumbnail");
//            }
//        }
//    }
//
//    private void saveImages(ShoeDetail shoeDetail, List<Image> images) {
//        for (Image image : images) {
//            try {
//                String thumbnailUrl = imageUploadService.uploadImageByName(String.valueOf(image.getImgName()));
//                image.setImgName(image.getImgName());
//                image.setImgUrl(thumbnailUrl);
//                image.setShoeDetail(shoeDetail);
//                imageRepository.save(image);
//            } catch (IOException e) {
//                throw new RuntimeException("Error uploading image");
//            }
//        }
//    }


}
