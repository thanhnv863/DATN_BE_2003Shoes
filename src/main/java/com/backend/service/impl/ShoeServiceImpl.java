package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.shoe.ShoeRequest;
import com.backend.dto.request.shoe.ShoeRequestUpdate;
import com.backend.dto.response.ShoeResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.dto.response.shoedetail.Meta;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Color;
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

import java.math.BigInteger;
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
    public ServiceResult<Shoe> updateShoe(ShoeRequestUpdate shoeRequestUpdate) {
        Optional<Shoe> shoeOptional = shoeRepository.findById(shoeRequestUpdate.getId());
        if (shoeOptional.isPresent()){
            if(shoeRequestUpdate.getName() == null || (shoeRequestUpdate.getName() != null && shoeRequestUpdate.getName().trim().isEmpty())){
                return new ServiceResult<>(AppConstant.BAD_REQUEST,"The name of shoe not valid!", null);
            }else {
                Shoe shoeExits = shoeOptional.get();
                shoeExits.setId(shoeExits.getId());
                shoeExits.setName(shoeRequestUpdate.getName().trim());
                shoeExits.setCreatedAt(shoeExits.getCreatedAt());

                Calendar calendar = Calendar.getInstance();
                shoeExits.setUpdatedAt(calendar.getTime());

                shoeExits.setStatus(shoeRequestUpdate.getStatus());
                Shoe shoeUpdate = shoeRepository.save(shoeExits);
                return new ServiceResult<>(AppConstant.SUCCESS,"The shoe update succesfully!", shoeUpdate);
            }
        }else {
            return new ServiceResult<>(AppConstant.BAD_REQUEST,"The shoe not found!", null);
        }
    }

    @Override
    public ServiceResult<Shoe> deleteShoe(ShoeRequestUpdate shoeRequestUpdate) {
        Optional<Shoe> shoeOptional = shoeRepository.findById(shoeRequestUpdate.getId());
        if (shoeOptional.isPresent()){
            Shoe shoeExits = shoeOptional.get();
            shoeExits.setStatus(0);
            shoeRepository.save(shoeExits);
            return new ServiceResult<>(AppConstant.SUCCESS,"The shoe delete succesfully!", null);
        }else {
            return new ServiceResult<>(AppConstant.BAD_REQUEST,"The shoe not found!", null);
        }
    }

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
            resultItem.setStatus(shoeDetail.getStatus());
//            resultItem.setThumbnail(shoeDetail.getThumbnails().get(0).getImgUrl());
            if (!shoeDetail.getThumbnails().isEmpty()) {
                resultItem.setThumbnail(shoeDetail.getThumbnails().get(0).getImgUrl());
            } else {
                resultItem.setThumbnail("");
            }
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
        Optional<Shoe> optionalShoe = shoeRepository.findByNameShoe(shoeRequest.getName().trim());
        if (optionalShoe.isPresent()) {
            if (optionalShoe.get().getStatus() == 0) {
                Shoe shoe = optionalShoe.get();
                shoe.setStatus(1);
                Shoe shoeUpdate = shoeRepository.save(shoe);
                return new ServiceResult(AppConstant.SUCCESS, "Shoe updated succesfully!", shoeUpdate);
            } else {
                return new ServiceResult(AppConstant.FAIL, "Shoe already exits!", null);
            }
        } else {
            if(shoeRequest.getName() == null || (shoeRequest.getName() != null && shoeRequest.getName().trim().isEmpty())){
                return new ServiceResult<>(AppConstant.BAD_REQUEST,"The name of shoe not valid!", null);
            }else {
                Shoe shoe = new Shoe();
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                shoe.setName(shoeRequest.getName().trim());
                shoe.setStatus(1);
                shoe.setCreatedAt(date);
                shoe.setUpdatedAt(date);
                return new ServiceResult(AppConstant.SUCCESS, "Success", shoeRepository.save(shoe));
            }
        }

    }

    @Override
    public ServiceResult<Shoe> getShoeByName(String name) {
        Optional shoe = shoeRepository.findByNameShoe(name);
        if (shoe.isPresent()) {
            return new ServiceResult(AppConstant.SUCCESS, "get data successfully!", shoe);
        }else {
            return new ServiceResult(AppConstant.NOT_FOUND, "Name of shoe not found", null);
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

}
