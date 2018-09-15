package com.example.nonsleeping.transferphone._interfaces;

import com.example.nonsleeping.transferphone.dto.ContactInfor;

import java.util.List;

public interface IF_GetContacts {
    void onFail();
    void onSuccess(List<ContactInfor> list);
}
