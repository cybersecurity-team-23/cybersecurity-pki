package com.example.pki.dto;

import lombok.Getter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;

@Getter
public class X500NameDto {
    private final String email;
    private final String commonName;
    private final String organisationalUnit;
    private final String organisation;
    private final String location;
    private final String state;
    private final String country;

    public X500NameDto(X500Name x500Name) {
        email = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.E)[0].getFirst().getValue());
        commonName = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.CN)[0].getFirst().getValue());
        organisationalUnit = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue());
        organisation = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.O)[0].getFirst().getValue());
        location = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.L)[0].getFirst().getValue());
        state = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.ST)[0].getFirst().getValue());
        country = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.C)[0].getFirst().getValue());
    }
}
