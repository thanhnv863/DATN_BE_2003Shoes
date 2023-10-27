package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.AddressRequest;
import com.backend.dto.response.AddressResponse;
import com.backend.entity.Address;

import java.util.List;
import java.util.Optional;

public interface IAddressService {
    ServiceResult<AddressResponse> addAddress(AddressRequest addressRequest);

    ServiceResult<Address> updateAddress(AddressRequest addressRequest);

    String validateAddress(AddressRequest addressRequest);

    AddressResponse convertToResponse(Address address);

    ServiceResult<AddressResponse> result(String mess);

    ServiceResult<List<AddressResponse>> getAllAddress();

    ServiceResult<Address> deleteAddress(AddressRequest addressRequest);

    ServiceResult<List<Address>> searchNameClient(String name);


}
