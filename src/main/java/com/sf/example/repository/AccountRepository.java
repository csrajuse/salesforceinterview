package com.sf.example.repository;

import com.sf.example.entity.Account;
import com.sf.example.util.AccountConstants;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is only for Account object. Before using the . Its an example class if we were to use in the normal conventional way.
 * The calls can be made from AnyObjectRepository instead of this class.
 */

@Lazy
@Service
public class AccountRepository extends BaseRepository{

    public AccountRepository(){
        this.setPublicKey("account");
    }

    @PostConstruct
    void init(){
        mapTheColumns();
    }

    public Account save(Account account){
        String id = account.getId();
        if(id == null){
            id = generateId();
            account.setId(id);
            this.insert(transferObjectToMap(account));
        }
        else{
            if(find(account.getId())==null){
                this.insert(transferObjectToMap(account));
            }else{
                this.update(transferObjectToMap(account));
            }
        }
        return account;
    }

    Map<String,String> transferObjectToMap(Account account){
        Map<String,String> objectMap = super.transferObjectToMap(account);
        objectMap.put(AccountConstants.SHIPPINGLATITUDE,account.getShippingLatitude());
        objectMap.put(AccountConstants.BILLINGCITY,account.getBillingCity());
        objectMap.put(AccountConstants.JIGSAWCOMPANYID,account.getJigsawCompanyId());
        objectMap.put(AccountConstants.SLA__c,account.getSLA__c());
        objectMap.put(AccountConstants.NAICSCODE,account.getNaicsCode());
        objectMap.put(AccountConstants.INDUSTRY,account.getIndustry());
        objectMap.put(AccountConstants.OPERATINGHOURSID,account.getOperatingHoursId());
        objectMap.put(AccountConstants.TICKERSYMBOL,account.getTickerSymbol());
        objectMap.put(AccountConstants.BILLINGGEOCODEACCURACY,account.getBillingGeocodeAccuracy());
        objectMap.put(AccountConstants.ACCOUNTSOURCE,account.getAccountSource());
        objectMap.put(AccountConstants.BILLINGPOSTALCODE,account.getBillingPostalCode());
        objectMap.put(AccountConstants.DANDBCOMPANYID,account.getDandbCompanyId());
        objectMap.put(AccountConstants.PHOTOURL,account.getPhotoUrl());
        objectMap.put(AccountConstants.MASTERRECORDID,account.getMasterRecordId());
        objectMap.put(AccountConstants.SHIPPINGGEOCODEACCURACY,account.getShippingGeocodeAccuracy());
        objectMap.put(AccountConstants.DUNSNUMBER,account.getDunsNumber());
        objectMap.put(AccountConstants.SIC,account.getSic());
        objectMap.put(AccountConstants.SHIPPINGSTREET,account.getShippingStreet());
        objectMap.put(AccountConstants.CLEANSTATUS,account.getCleanStatus());
        objectMap.put(AccountConstants.SHIPPINGPOSTALCODE,account.getShippingPostalCode());
        objectMap.put(AccountConstants.YEARSTARTED,account.getYearStarted());
        objectMap.put(AccountConstants.CUSTOMERPRIORITY__C,account.getCustomerPriority__c());
        objectMap.put(AccountConstants.SHIPPINGSTATE,account.getShippingState());
        objectMap.put(AccountConstants.SICDESC,account.getSicDesc());
        objectMap.put(AccountConstants.BILLINGSTATE,account.getBillingState());
        objectMap.put(AccountConstants.ANNUALREVENUE,account.getAnnualRevenue());
        objectMap.put(AccountConstants.JIGSAW,account.getJigsaw());
        objectMap.put(AccountConstants.ACTIVE__C,account.getActive__c());
        objectMap.put(AccountConstants.SITE,account.getSite());
        objectMap.put(AccountConstants.OWNERSHIP,account.getOwnership());
        objectMap.put(AccountConstants.DESCRIPTION,account.getDescription());
        objectMap.put(AccountConstants.RATING,account.getRating());
        objectMap.put(AccountConstants.WEBSITE,account.getWebsite());
        objectMap.put(AccountConstants.BILLINGLATITUDE,account.getBillingLatitude());
        objectMap.put(AccountConstants.NUMBEROFEMPLOYEES,account.getNumberOfEmployees());
        objectMap.put(AccountConstants.BILLINGADDRESS,account.getBillingAddress());
        objectMap.put(AccountConstants.OWNERID,account.getOwnerId());
        objectMap.put(AccountConstants.SLASERIALNUMBER__C,account.getSLASerialNumber__c());
        objectMap.put(AccountConstants.BILLINGLONGITUDE,account.getBillingLongitude());
        objectMap.put(AccountConstants.TRADESTYLE,account.getTradestyle());
        objectMap.put(AccountConstants.PHONE,account.getPhone());
        objectMap.put(AccountConstants.NUMBEROFLOCATIONS__C,account.getNumberofLocations__c());
        objectMap.put(AccountConstants.SHIPPINGCOUNTRY,account.getShippingCountry());
        objectMap.put(AccountConstants.SHIPPINGCITY,account.getShippingCity());
        objectMap.put(AccountConstants.PARENTID,account.getParentId());
        objectMap.put(AccountConstants.NAICSDESC,account.getNaicsDesc());
        objectMap.put(AccountConstants.ACCOUNTNUMBER,account.getAccountNumber());
        objectMap.put(AccountConstants.TYPE,account.getType());
        objectMap.put(AccountConstants.BILLINGCOUNTRY,account.getBillingCountry());
        objectMap.put(AccountConstants.SLAEXPIRATIONDATE__C,account.getSLAExpirationDate__c());
        objectMap.put(AccountConstants.BILLINGSTREET,account.getBillingStreet());
        objectMap.put(AccountConstants.SHIPPINGADDRESS,account.getShippingAddress());
        objectMap.put(AccountConstants.FAX,account.getFax());
        objectMap.put(AccountConstants.SHIPPINGLONGITUDE,account.getShippingLongitude());

        return objectMap;
    }


    Account transferMapToObject(Map<String, String> map){
        Account account = new Account();
        transferMapToObject(map,account);
        account.setShippingLatitude(map.get(AccountConstants.SHIPPINGLATITUDE));
        account.setBillingCity(map.get(AccountConstants.BILLINGCITY));
        account.setJigsawCompanyId(map.get(AccountConstants.JIGSAWCOMPANYID));
        account.setSLA__c(map.get(AccountConstants.SLA__c));
        account.setNaicsCode(map.get(AccountConstants.NAICSCODE));
        account.setIndustry(map.get(AccountConstants.INDUSTRY));
        account.setOperatingHoursId(map.get(AccountConstants.OPERATINGHOURSID));
        account.setTickerSymbol(map.get(AccountConstants.TICKERSYMBOL));
        account.setBillingGeocodeAccuracy(map.get(AccountConstants.BILLINGGEOCODEACCURACY));
        account.setAccountSource(map.get(AccountConstants.ACCOUNTSOURCE));
        account.setBillingPostalCode(map.get(AccountConstants.BILLINGPOSTALCODE));
        account.setDandbCompanyId(map.get(AccountConstants.DANDBCOMPANYID));
        account.setPhotoUrl(map.get(AccountConstants.PHOTOURL));
        account.setMasterRecordId(map.get(AccountConstants.MASTERRECORDID));
        account.setShippingGeocodeAccuracy(map.get(AccountConstants.SHIPPINGGEOCODEACCURACY));
        account.setDunsNumber(map.get(AccountConstants.DUNSNUMBER));
        account.setSic(map.get(AccountConstants.SIC));
        account.setShippingStreet(map.get(AccountConstants.SHIPPINGSTREET));
        account.setCleanStatus(map.get(AccountConstants.CLEANSTATUS));
        account.setShippingPostalCode(map.get(AccountConstants.SHIPPINGPOSTALCODE));
        account.setYearStarted(map.get(AccountConstants.YEARSTARTED));
        account.setCustomerPriority__c(map.get(AccountConstants.CUSTOMERPRIORITY__C));
        account.setShippingState(map.get(AccountConstants.SHIPPINGSTATE));
        account.setSicDesc(map.get(AccountConstants.SICDESC));
        account.setBillingState(map.get(AccountConstants.BILLINGSTATE));
        account.setAnnualRevenue(map.get(AccountConstants.ANNUALREVENUE));
        account.setJigsaw(map.get(AccountConstants.JIGSAW));
        account.setActive__c(map.get(AccountConstants.ACTIVE__C));
        account.setSite(map.get(AccountConstants.SITE));
        account.setOwnership(map.get(AccountConstants.OWNERSHIP));
        account.setDescription(map.get(AccountConstants.DESCRIPTION));
        account.setRating(map.get(AccountConstants.RATING));
        account.setWebsite(map.get(AccountConstants.WEBSITE));
        account.setBillingLatitude(map.get(AccountConstants.BILLINGLATITUDE));
        account.setNumberOfEmployees(map.get(AccountConstants.NUMBEROFEMPLOYEES));
        account.setBillingAddress(map.get(AccountConstants.BILLINGADDRESS));
        account.setOwnerId(map.get(AccountConstants.OWNERID));
        account.setSLASerialNumber__c(map.get(AccountConstants.SLASERIALNUMBER__C));
        account.setBillingLongitude(map.get(AccountConstants.BILLINGLONGITUDE));
        account.setTradestyle(map.get(AccountConstants.TRADESTYLE));
        account.setPhone(map.get(AccountConstants.PHONE));
        account.setNumberofLocations__c(map.get(AccountConstants.NUMBEROFLOCATIONS__C));
        account.setShippingCountry(map.get(AccountConstants.SHIPPINGCOUNTRY));
        account.setShippingCity(map.get(AccountConstants.SHIPPINGCITY));
        account.setParentId(map.get(AccountConstants.PARENTID));
        account.setNaicsDesc(map.get(AccountConstants.NAICSDESC));
        account.setAccountNumber(map.get(AccountConstants.ACCOUNTNUMBER));
        account.setType(map.get(AccountConstants.TYPE));
        account.setBillingCountry(map.get(AccountConstants.BILLINGCOUNTRY));
        account.setSLAExpirationDate__c(map.get(AccountConstants.SLAEXPIRATIONDATE__C));
        account.setBillingStreet(map.get(AccountConstants.BILLINGSTREET));
        account.setShippingAddress(map.get(AccountConstants.SHIPPINGADDRESS));
        account.setFax(map.get(AccountConstants.FAX));
        account.setShippingLongitude(map.get(AccountConstants.SHIPPINGLONGITUDE));
        return account;
    }


    @Override
    public Account find(String id) {
        Map<String,String> map = getObject(id);
        return map == null || map.isEmpty() ? null : transferMapToObject(getObject(id));
    }

    @Override
    public List<Account> findAll(){
        List<Account> accountsList = new ArrayList<>();
        getAllObjects().stream().forEach(objMap->accountsList.add(transferMapToObject((Map<String,String>)objMap)));
        return accountsList;
    }
}
