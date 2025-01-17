package com.DreamCoder.DreamLeaf.service;



import com.DreamCoder.DreamLeaf.repository.StoreHygradeRepository;
import com.DreamCoder.DreamLeaf.repository.StoreRepository;
import com.DreamCoder.DreamLeaf.req.StoreHygradeReq;
import com.DreamCoder.DreamLeaf.req.StoreReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiManager {

    @Autowired
    private final StoreRepository storeRepository;
    private final StoreHygradeRepository storeHygradeRepository;

    private String makeUrl(String storeType, String key, String dataType, int pIndex, int pSize){
        StringBuffer sb=new StringBuffer();
        sb.append("https://openapi.gg.go.kr/");
        sb.append(storeType);
        sb.append("?KEY=");
        sb.append(key);
        sb.append("&Type=");
        sb.append(dataType);
        sb.append("&pIndex=");
        sb.append(pIndex);
        sb.append("&pSize=");
        sb.append(pSize);

        return sb.toString();
    }

    public void saveGDreamCardApi(){
        String result="";
        int pIndex=1;
        Long totalCnt;
        try{
            do{
            URL url=new URL(makeUrl("GDreamCard", "e67be2abc4464ffcb547fe1fecc6d138","json", pIndex, 1000));
            BufferedReader bf;
            bf=new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            result=bf.readLine();


            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=(JSONObject) jsonParser.parse(result);
            JSONArray gDreamCard=(JSONArray) jsonObject.get("GDreamCard");      //전체 json get(size=2(head, row))

            //casting head
            JSONObject head=(JSONObject)gDreamCard.get(0);
            JSONArray head2=(JSONArray)head.get("head");
            log.info("head={}", head2);
            totalCnt=(Long)((JSONObject)head2.get(0)).get("list_total_count");
            log.info("cnt={}", totalCnt);
            JSONObject apiResult=(JSONObject)((JSONObject)head2.get(1)).get("RESULT");
            String resultCode=(String)apiResult.get("CODE");
            log.info("resCode={}", resultCode);

            //casting body(row)
            JSONObject row=(JSONObject)gDreamCard.get(1);
            JSONArray infoArr=(JSONArray)row.get("row");



            for(int i=0;i<infoArr.size();i++) {
                JSONObject temp = (JSONObject) infoArr.get(i);

                //check zip, location if null
                int zipcd;
                double lat, logt;
                String lotno;

                if ((String) temp.get("REFINE_LOTNO_ADDR") == null) {
                    lotno = "";
                } else {
                    lotno = (String) temp.get("REFINE_LOTNO_ADDR");
                }
                if ((String) temp.get("REFINE_ZIP_CD") == null) {
                    zipcd = 0;
                } else {
                    zipcd = Integer.parseInt((String) temp.get("REFINE_ZIP_CD"));
                }
                if ((String) temp.get("REFINE_WGS84_LAT") == null) {
                    continue;
                } else {
                    lat = Double.parseDouble((String) temp.get("REFINE_WGS84_LAT"));
                }
                if ((String) temp.get("REFINE_WGS84_LOGT") == null) {
                    continue;
                } else {
                    logt = Double.parseDouble((String) temp.get("REFINE_WGS84_LOGT"));
                }

                StoreReq checkFor1=new StoreReq((String) temp.get("FACLT_NM"),
                        zipcd,
                        (String) temp.get("REFINE_ROADNM_ADDR"),
                        lotno,
                        lat, logt, 0, "", "");
                StoreReq checkFor2=new StoreReq((String) temp.get("FACLT_NM"),
                        zipcd,
                        (String) temp.get("REFINE_ROADNM_ADDR"),
                        lotno,
                        lat, logt, 2, "", "");
                if(storeRepository.hasAnotherType(checkFor1)){
                    storeRepository.updatePaymentTo2(checkFor1);
                }
                else if(storeRepository.hasAnotherType(checkFor2)){

                }
                else{
                    StoreReq infoObj = new StoreReq((String) temp.get("FACLT_NM"),
                            zipcd,
                            (String) temp.get("REFINE_ROADNM_ADDR"),
                            lotno,
                            lat, logt, 1, "", "");
                    storeRepository.save(infoObj);
                }
            }
            pIndex++;
            }while(pIndex<=(totalCnt/1000)+1);



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveGoodStoreApi(){
        String result="";
        int pIndex=1;
        Long totalCnt;
        try{
            do{
                URL url=new URL(makeUrl("GGGOODINFLSTOREST", "fb1025fa7b1145fbbbc2a843c0d8c10e","json", pIndex, 1000));
                BufferedReader bf;
                bf=new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                result=bf.readLine();


                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=(JSONObject) jsonParser.parse(result);
                JSONArray goodStore=(JSONArray) jsonObject.get("GGGOODINFLSTOREST");      //전체 json get(size=2(head, row))

                //casting head
                JSONObject head=(JSONObject)goodStore.get(0);
                JSONArray head2=(JSONArray)head.get("head");
                log.info("head={}", head2);
                totalCnt=(Long)((JSONObject)head2.get(0)).get("list_total_count");
                log.info("cnt={}", totalCnt);
                JSONObject apiResult=(JSONObject)((JSONObject)head2.get(1)).get("RESULT");
                String resultCode=(String)apiResult.get("CODE");
                log.info("resCode={}", resultCode);

                //casting body(row)
                JSONObject row=(JSONObject)goodStore.get(1);
                JSONArray infoArr=(JSONArray)row.get("row");



                for(int i=0;i<infoArr.size();i++){
                    JSONObject temp=(JSONObject)infoArr.get(i);

                    //check zip, location if null
                    int zipcd;
                    double lat, logt;
                    String roadno,lotno,prodName, prodTarget;

                    //음식점이 아닌 가게는 추가하지 않음
                    if(!((String)temp.get("INDUTYPE_NM")).equals("식음료")){
                        continue;
                    }

                    //정보가 없는 가게에 대한 String 처리
                    if((String)temp.get("REFINE_ROADNM_ADDR")==null){
                        roadno="";
                    }
                    else{
                        roadno=(String)temp.get("REFINE_ROADNM_ADDR");
                    }
                    if((String)temp.get("REFINE_LOTNO_ADDR")==null){
                        lotno="";
                    }
                    else{
                        lotno=(String)temp.get("REFINE_LOTNO_ADDR");
                    }
                    if((String)temp.get("REFINE_ZIPNO")==null){
                        zipcd=0;
                    }
                    else{
                        zipcd=Integer.parseInt((String)temp.get("REFINE_ZIPNO"));
                    }

                    //위치 정보가 없는 가게는 추가하지 않음
                    if((String)temp.get("REFINE_WGS84_LAT")==null){
                        continue;
                    }
                    else{
                        lat=Double.parseDouble((String)temp.get("REFINE_WGS84_LAT"));
                    }
                    if((String)temp.get("REFINE_WGS84_LOGT")==null){
                        continue;
                    }
                    else{
                        logt=Double.parseDouble((String)temp.get("REFINE_WGS84_LOGT"));
                    }
                    if((String)temp.get("PROVSN_PRODLST_NM")==null){
                        prodName="";
                    }
                    else{
                        prodName=(String)temp.get("PROVSN_PRODLST_NM");
                    }
                    if((String)temp.get("PROVSN_TRGT_NM1")==null){
                        prodTarget="";
                    }
                    else{
                        prodTarget=(String)temp.get("PROVSN_TRGT_NM1");
                    }
                    if((String)temp.get("PROVSN_TRGT_NM2")!=null){
                        prodTarget+=(String)temp.get("PROVSN_TRGT_NM2");
                    }

                    StoreReq checkFor1=new StoreReq((String)temp.get("CMPNM_NM"),
                            zipcd,
                            roadno,
                            lotno,
                            lat, logt,1, prodName, prodTarget);
                    StoreReq checkFor2=new StoreReq((String)temp.get("CMPNM_NM"),
                            zipcd,
                            roadno,
                            lotno,
                            lat, logt,2, prodName, prodTarget);
                    if(storeRepository.hasAnotherType(checkFor1)){
                        storeRepository.updatePaymentTo2(checkFor1);
                    }
                    else if(storeRepository.hasAnotherType(checkFor2)){

                    }
                    else{
                        StoreReq infoObj=new StoreReq((String)temp.get("CMPNM_NM"),
                                zipcd,
                                roadno,
                                lotno,
                                lat, logt,0, prodName, prodTarget);
                        storeRepository.save(infoObj);
                    }
                }
                pIndex++;
            }while(pIndex<=(totalCnt/1000)+1);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void saveHygieneApi(){
        String result="";
        int pIndex=1;
        Long totalCnt;
        try{
            do{
                URL url=new URL(makeUrl("RestrtSanittnGradStus", "1cbb5970a6a3461b8e4282e78a548c30","json", pIndex, 1000));
                BufferedReader bf;
                bf=new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                result=bf.readLine();


                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=(JSONObject) jsonParser.parse(result);
                JSONArray goodStore=(JSONArray) jsonObject.get("RestrtSanittnGradStus");      //전체 json get(size=2(head, row))

                //casting head
                JSONObject head=(JSONObject)goodStore.get(0);
                JSONArray head2=(JSONArray)head.get("head");
                log.info("head={}", head2);
                totalCnt=(Long)((JSONObject)head2.get(0)).get("list_total_count");
                log.info("cnt={}", totalCnt);
                JSONObject apiResult=(JSONObject)((JSONObject)head2.get(1)).get("RESULT");
                String resultCode=(String)apiResult.get("CODE");
                log.info("resCode={}", resultCode);

                //casting body(row)
                JSONObject row=(JSONObject)goodStore.get(1);
                JSONArray infoArr=(JSONArray)row.get("row");



                for(int i=0;i<infoArr.size();i++){
                    JSONObject temp=(JSONObject)infoArr.get(i);

                    //check location if null
                    double lat, logt;
                    String roadno,lotno;


                    //정보가 없는 가게에 대한 String 처리
                    if((String)temp.get("REFINE_ROADNM_ADDR")==null){
                        roadno="";
                    }
                    else{
                        roadno=(String)temp.get("REFINE_ROADNM_ADDR");
                    }
                    if((String)temp.get("REFINE_LOTNO_ADDR")==null){
                        lotno="";
                    }
                    else{
                        lotno=(String)temp.get("REFINE_LOTNO_ADDR");
                    }


                    //위치 정보가 없는 가게는 추가하지 않음
                    if((String)temp.get("REFINE_WGS84_LAT")==null){
                        continue;
                    }
                    else{
                        lat=Double.parseDouble((String)temp.get("REFINE_WGS84_LAT"));
                    }
                    if((String)temp.get("REFINE_WGS84_LOGT")==null){
                        continue;
                    }
                    else{
                        logt=Double.parseDouble((String)temp.get("REFINE_WGS84_LOGT"));
                    }

                    StoreHygradeReq infoObj=new StoreHygradeReq((String)temp.get("ENTRPS_NM"),
                            (String)temp.get("APPONT_GRAD"),
                            roadno,
                            lotno,
                            lat, logt);
                    storeHygradeRepository.save(infoObj);

                }
                pIndex++;
            }while(pIndex<=(totalCnt/1000)+1);
        }catch(Exception e){
            e.printStackTrace();
        }

    }






}
