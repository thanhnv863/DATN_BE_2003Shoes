package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.AddressRequest;
import com.backend.dto.response.AddressResponse;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.entity.Address;

import java.util.List;

public interface IAddressService {
    ServiceResult<AddressResponse> addAddress(AddressRequest addressRequest);

    ServiceResult<Address> updateAddress(AddressRequest addressRequest);

    String validateAddress(AddressRequest addressRequest);

    AddressResponse convertToResponse(Address address);

    ServiceResult<AddressResponse> result(String mess);

    ServiceResult<List<AddressResponse>> getAllAddress();

    ServiceResult<Address> deleteAddress(AddressRequest addressRequest);
}
