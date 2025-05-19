package org.omnione.did.issuer.v1.agent.service.sample;

import com.google.gson.Gson;
import org.omnione.did.base.datamodel.data.zkp.ZkpCredentionDefRows;
import org.omnione.did.base.datamodel.data.zkp.ZkpLadgerRequestData;
import org.omnione.did.base.datamodel.data.zkp.ZkpLadgerResponseData;
import org.omnione.did.base.datamodel.data.zkp.ZkpSchemaRows;
import org.omnione.did.zkp.core.manager.ZkpCredentialManager;
import org.omnione.did.zkp.core.manager.ZkpCredentialMetadataManager;
import org.omnione.did.zkp.crypto.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.zkp.datamodel.credential.*;
import org.omnione.did.zkp.datamodel.credentialoffer.CredentialOffer;
import org.omnione.did.zkp.datamodel.credentialoffer.KeyCorrectnessProof;
import org.omnione.did.zkp.datamodel.credentialrequest.CredentialRequest;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.enums.PredicateType;
import org.omnione.did.zkp.datamodel.proofrequest.AttributeInfo;
import org.omnione.did.zkp.datamodel.proofrequest.PredicateInfo;
import org.omnione.did.zkp.datamodel.schema.AttributeDef;
import org.omnione.did.zkp.datamodel.schema.AttributeType;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.schema.Namespace;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.omnione.did.zkp.exception.ZkpException;
import org.omnione.did.zkp.wallet.enums.ZkpWalletEncryptType;
import org.omnione.did.zkp.wallet.key.ZkpWalletManagerFactory;
import org.omnione.did.zkp.wallet.key.ZkpWalletManagerInterface;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import static org.omnione.did.zkp.datamodel.util.GsonWrapper.getGsonPrettyPrinting;

public class ZkpSampleConstants {
    // MDL VL VL2 3개 생성
    // MDL과 VL의 Issuer DID
    public static String ISSUER_DID = "did:omn:NcYxiDXkpYi6ov5FcYDi1e";

    // 1번 MDL
    public static String SCHEMA_ID_MDL = "did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:mdl:1.0";
    public static String CRED_DEF_ID_MDL = "did:omn:NcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:mdl:1.0:Tag1";
    public static String SCHEMA_NAME_MDL = "mdl";

    // 2번 VL
    public static String SCHEMA_ID_VL = "did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:vl:1.0";
    public static String CRED_DEF_ID_VL = "did:omn:NcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:vl:1.0:Tag1";
    public static String SCHEMA_NAME_VL = "vl";

    // 3번 VL2
    public static String ISSUER_DID_VL2 = "did:omn:XcYxiDXkpYi6ov5FcYDi1e";
    public static String CRED_DEF_ID_VL2 = "did:omn:XcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:vl:1.0:Tag1";

    public static String VERSION = "1.0";
    public static final String TAG = "Tag1";


    // Proof Request의 Predicate 조건
    public static final int AGE_CONDITION = 20250101;
    public static final int SALARY_CONDITION = 50000;

    // zkp wallet
    public static final String WALLET_FILE_PATH = "/tmp/issuer.wallet";
    public static final String WALLET_PASSWORD = "123456";
    public static final String WALLET_TEST_KEY_ID_MDL = "zkpMDL";
    public static final String WALLET_TEST_KEY_ID_VL = "zkpVL";
    public static final String WALLET_TEST_KEY_ID_VL2 = "zkpVL2";

    static {
        credentialSchema();
        createZkpKeys();
        credentialDefinition();
    }

    // 1번 MDL Schema attribute list
    public static List<String> getAttributeListMdl() {

        List<String> attributeList = new LinkedList<String>();
        attributeList.add("MDLNS.zkpsex");
        attributeList.add("MDLNS.zkpbirth");
        attributeList.add("MDLNS.zkpasort");
        attributeList.add("MDLNS.zkpaddr");

        return attributeList;
    }

    // 2번 VL Schema attribute list
    public static List<String> getAttributeListVl() {

        List<String> attributeList = new LinkedList<String>();
        attributeList.add("VLNS.voterID");
        attributeList.add("VLNS.citizenship");
        attributeList.add("VLNS.region");
        attributeList.add("VLNS.birth");
        return attributeList;
    }

    // 1번 MDL Schema attribute type
    public static List<AttributeType> getAttributeTypeListMdl() {

        List<AttributeType> attributeTypeList = new LinkedList<AttributeType>();
        AttributeType attributeTypeMdl = new AttributeType();

        Namespace namespace = new Namespace();
        namespace.setName("MDL NameSpace");
        namespace.setId("MDLNS");
        attributeTypeMdl.setNamespace(namespace);

        List<AttributeDef> attributeDefList = new ArrayList<AttributeDef>();
        AttributeDef attributeDef1 = new AttributeDef();
        AttributeDef attributeDef2 = new AttributeDef();
        AttributeDef attributeDef3 = new AttributeDef();
        AttributeDef attributeDef4 = new AttributeDef();

        attributeDef1.setType(AttributeDef.ATTR_TYPE.STRING);
        attributeDef1.setLabel("zkpsex");
        attributeDef1.setCaption("성별");
        attributeDefList.add(attributeDef1);

        attributeDef2.setType(AttributeDef.ATTR_TYPE.NUMBER);
        attributeDef2.setLabel("zkpbirth");
        attributeDef2.setCaption("생년월일");
        attributeDefList.add(attributeDef2);

        attributeDef3.setType(AttributeDef.ATTR_TYPE.STRING);
        attributeDef3.setLabel("zkpasort");
        attributeDef3.setCaption("뭔지모름");
        attributeDefList.add(attributeDef3);

        attributeDef4.setType(AttributeDef.ATTR_TYPE.STRING);
        attributeDef4.setLabel("zkpaddr");
        attributeDef4.setCaption("주소");
        Map<String, String> i18n = new HashMap<>();
        i18n.put("ko", "주소");
        i18n.put("en", "address");
        attributeDef4.setI18n(i18n);
        attributeDefList.add(attributeDef4);

        attributeTypeMdl.setItems(attributeDefList);

        attributeTypeList.add(attributeTypeMdl);
        return attributeTypeList;
    }

    // 2번 VL Schema attribute type
    public static List<AttributeType> getAttributeTypeListVl() {

        List<AttributeType> attributeTypeList = new LinkedList<AttributeType>();
        AttributeType attributeTypeVl = new AttributeType();

        Namespace namespace = new Namespace();
        namespace.setName("VL NameSpace");
        namespace.setId("VLNS");
        attributeTypeVl.setNamespace(namespace);

        List<AttributeDef> attributeDefList = new ArrayList<AttributeDef>();
        AttributeDef attributeDef1 = new AttributeDef();
        AttributeDef attributeDef2 = new AttributeDef();
        AttributeDef attributeDef3 = new AttributeDef();
        AttributeDef attributeDef4 = new AttributeDef();

        attributeDef1.setType(AttributeDef.ATTR_TYPE.STRING);
        attributeDef1.setLabel("voterID");
        attributeDef1.setCaption("투표아이디");
        attributeDefList.add(attributeDef1);

        attributeDef2.setType(AttributeDef.ATTR_TYPE.NUMBER);
        attributeDef2.setLabel("citizenship");
        attributeDef2.setCaption("몰라");
        attributeDefList.add(attributeDef2);

        attributeDef3.setType(AttributeDef.ATTR_TYPE.STRING);
        attributeDef3.setLabel("region");
        attributeDef3.setCaption("지역");
        attributeDefList.add(attributeDef3);

        attributeDef4.setType(AttributeDef.ATTR_TYPE.NUMBER);
        attributeDef4.setLabel("birth");
        attributeDef4.setCaption("생일");
        attributeDefList.add(attributeDef4);

        attributeTypeVl.setItems(attributeDefList);

        attributeTypeList.add(attributeTypeVl);
        return attributeTypeList;

    }


//    public static List<AttributeType> getAttributeTypeListMdl() {
//
//        List<AttributeType> attributeTypeList = new LinkedList<AttributeType>();
//        AttributeType attributeType1 = new AttributeType();
//        AttributeType attributeType2 = new AttributeType();
//        AttributeType attributeType3 = new AttributeType();
//        AttributeType attributeType4 = new AttributeType();
//
//        attributeType1.setLabel("zkpsex");
//        attributeType1.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType1);
//        attributeType2.setLabel("zkpbirth");
//        attributeType2.setType(AttributeType.Type.NUMBER);
//        attributeTypeList.add(attributeType2);
//        attributeType3.setLabel("zkpasort");
//        attributeType3.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType3);
//        attributeType4.setLabel("zkpaddr");
//        attributeType4.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType4);
//
//        return attributeTypeList;
//    }


//    public static List<AttributeType> getAttributeTypeListVl() {
//
//        List<AttributeType> attributeTypeList = new LinkedList<AttributeType>();
//        AttributeType attributeType1 = new AttributeType();
//        AttributeType attributeType2 = new AttributeType();
//        AttributeType attributeType3 = new AttributeType();
//        AttributeType attributeType4 = new AttributeType();
//
//        attributeType1.setLabel("voterID");
//        attributeType1.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType1);
//        attributeType2.setLabel("citizenship");
//        attributeType2.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType2);
//        attributeType3.setLabel("region");
//        attributeType3.setType(AttributeType.Type.STRING);
//        attributeTypeList.add(attributeType3);
//        attributeType4.setLabel("birth");
//        attributeType4.setType(AttributeType.Type.NUMBER);
//        attributeTypeList.add(attributeType4);
//
//        return attributeTypeList;
//    }


    // 1번 MDL Credential 값 - 임의로..
    public static LinkedHashMap<String, AttributeValue> genCredentialValueMdl(String schemaStr, String did) {
        String[] value = new String[]{"female", "20000101", "54321", "kimpo"};
        int cnt = 0;
        LinkedHashMap<String, AttributeValue> credentialValue = new LinkedHashMap<>();
        try {
            CredentialSchema schema = new Gson().fromJson(schemaStr, CredentialSchema.class);

            for (String attrName : schema.getAttrNames()) {
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setRaw(value[cnt]);
                credentialValue.put(attrName, attributeValue);
                cnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credentialValue;
    }

    // 2번 VL Credential 값 - 임의로..
    public static LinkedHashMap<String, AttributeValue> genCredentialValueVl(String schemaStr, String did) {
        String[] value = new String[]{"djpark0402", "시민권자", "서울시강서구", "19840402"};
        int cnt = 0;
        LinkedHashMap<String, AttributeValue> credentialValue = new LinkedHashMap<>();
        try {
            CredentialSchema schema = new Gson().fromJson(schemaStr, CredentialSchema.class);

            for (String attrName : schema.getAttrNames()) {
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setRaw(value[cnt]);
                credentialValue.put(attrName, attributeValue);
                cnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credentialValue;
    }

    // 3번 VL2 Credential 값 - 임의로..
    public static LinkedHashMap<String, AttributeValue> genCredentialValueVl2(String schemaStr, String did) {
        String[] value = new String[]{"sjkim", "영주권자", "김포", "19830518"};
        int cnt = 0;
        LinkedHashMap<String, AttributeValue> credentialValue = new LinkedHashMap<>();
        try {
            CredentialSchema schema = new Gson().fromJson(schemaStr, CredentialSchema.class);

            for (String attrName : schema.getAttrNames()) {
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setRaw(value[cnt]);
                credentialValue.put(attrName, attributeValue);
                cnt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credentialValue;
    }


    // 발급받은 3개의 credential에서 선택 제출 - 일반 attribute
    public static Map<String, AttributeInfo> getProofRequestAttribute() {

        Map<String, String> restriction = new HashMap<String, String>();
        Map<String, String> restriction2 = new HashMap<String, String>();
        Map<String, String> restriction3 = new HashMap<String, String>();
        restriction.put("credDefId", "did:omn:issuer:3:CL:did:omn:issuer:2:vl:1.0:Tag1");
        restriction2.put("credDefId", "did:omn:issuer:3:CL:did:omn:issuer:2:mdl:1.0:Tag1");
        restriction3.put("credDefId", "did:omn:XcYxiDXkpYi6ov5FcYDi1e:3:CL:did:omn:issuer:2:vl:1.0:Tag1");
        LinkedHashMap<String, AttributeInfo> attributeMap = new LinkedHashMap<String, AttributeInfo>();
        AttributeInfo attributeInfo1 = new AttributeInfo();
//        attributeInfo1.setName("zkpsex");
//        attributeInfo1.addRestriction(restriction);
//        attributeMap.put("attributeReferent1", attributeInfo1);
//
//        AttributeInfo attributeInfo2 = new AttributeInfo();
//        attributeInfo2.setName("zkpasort");
//        attributeInfo2.addRestriction(restriction);
//        attributeMap.put("attributeReferent2", attributeInfo2);
//
//        AttributeInfo attributeInfo3 = new AttributeInfo();
//        attributeInfo3.setName("zkpaddr");
//        attributeInfo3.addRestriction(restriction);
//        attributeMap.put("attributeReferent3", attributeInfo3);

//        AttributeInfo attributeInfo4 = new AttributeInfo();
//        attributeInfo4.setName("zkpphone");
//        attributeInfo4.addRestriction(restriction);
//        attributeMap.put("attributeReferent4", attributeInfo4);

        attributeInfo1.setName("voterID");
//        attributeInfo1.addRestriction(restriction);
        attributeMap.put("attributeReferent1", attributeInfo1);

        AttributeInfo attributeInfo2 = new AttributeInfo();
        attributeInfo2.setName("citizenship");
        attributeInfo2.addRestriction(restriction);
        attributeInfo2.addRestriction(restriction3);
        attributeMap.put("attributeReferent2", attributeInfo2);

        AttributeInfo attributeInfo3 = new AttributeInfo();
        attributeInfo3.setName("region");
        attributeInfo3.addRestriction(restriction);
        attributeInfo3.addRestriction(restriction3);
        attributeMap.put("attributeReferent3", attributeInfo3);

        AttributeInfo attributeInfo4 = new AttributeInfo();
        attributeInfo4.setName("name");
        //       attributeInfo4.addRestriction(restriction);
        attributeMap.put("attributeReferent4", attributeInfo4);

        AttributeInfo attributeInfo5 = new AttributeInfo();
        attributeInfo5.setName("zkpasort");
        attributeInfo5.addRestriction(restriction2);
        attributeMap.put("attributeReferent5", attributeInfo5);

        AttributeInfo attributeInfo6 = new AttributeInfo();
        attributeInfo6.setName("zkpaddr");
        attributeInfo6.addRestriction(restriction2);
        attributeMap.put("attributeReferent6", attributeInfo6);

        return attributeMap;
    }

    // 발급받은 3개의 credential에서 선택 제출 - predicate
    public static Map<String, PredicateInfo> getProofRequestPredicate() {

        LinkedHashMap<String, PredicateInfo> predicateMap = new LinkedHashMap<String, PredicateInfo>();
        PredicateInfo predicateInfo1 = new PredicateInfo();
//        predicateInfo1.setPType(PredicateType.LE);
//        predicateInfo1.setName("zkpbirth");
//        predicateInfo1.setPValue(ZkpSampleConstants.AGE_CONDITION);
//        Map<String, String> restriction = new HashMap<String, String>();
//        restriction.put("credDefId", "did:omn:issuer:3:CL:did:omn:issuer:2:mdl:1.0:Tag1");
//        predicateInfo1.addRestriction(restriction);
//        predicateMap.put("predicateReferent1", predicateInfo1);

//        PredicateInfo predicateInfo2 = new PredicateInfo();
//        predicateInfo2.setPType(PredicateType.GE);
//        predicateInfo2.setName("zkpsalary");
//        predicateInfo2.setPValue(ZkpSampleConstants.SALARY_CONDITION);
//        predicateInfo2.addRestriction(restriction);
//        predicateMap.put("predicateReferent1", predicateInfo2);

        predicateInfo1.setPType(PredicateType.LE);
        predicateInfo1.setName("birth");
        predicateInfo1.setPValue(ZkpSampleConstants.AGE_CONDITION);
        Map<String, String> restriction = new HashMap<String, String>();
        restriction.put("credDefId", "did:omn:issuer:3:CL:did:omn:issuer:2:vl:1.0:Tag1");
        predicateInfo1.addRestriction(restriction);
        predicateMap.put("predicateReferent1", predicateInfo1);

        return predicateMap;
    }

    // Credential Definition 생성
    public static void credentialDefinition() {
        System.out.println("loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL) = " + loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL));
        if (loadZKPData(ZkpSampleConstants.CRED_DEF_ID_MDL) != null) {
            return;
        }
        //예시로 mdl / vl / vl2 각각 3개의 def를 생성
        CredentialDefinition credentialDefinitionMdl = null;
        CredentialDefinition credentialDefinitionVl = null;
        CredentialDefinition credentialDefinitionVl2 = null;

        // mdl schema 조회
        String schemaStrMdl = loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL);
        CredentialSchema schemaMdl = new Gson().fromJson(schemaStrMdl, CredentialSchema.class);

        // vl schema 조회
        String schemaStrVl = loadZKPData(ZkpSampleConstants.SCHEMA_ID_VL);
        CredentialSchema schemaVl = new Gson().fromJson(schemaStrVl, CredentialSchema.class);

        CredentialPrimaryPublicKey credentialPrimaryPublicKeyMdl = null;
        CredentialPrimaryPublicKey credentialPrimaryPublicKeyVl = null;
        CredentialPrimaryPublicKey credentialPrimaryPublicKeyVl2 = null;

        try {
            // 블록체인에 저장된 스키마 기반으로 wallet에서 공개키를 가져와서 def 생성
            ZkpWalletManagerInterface walletManager = null;

            walletManager = ZkpWalletManagerFactory.getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            walletManager.connect(ZkpSampleConstants.WALLET_FILE_PATH, ZkpSampleConstants.WALLET_PASSWORD.toCharArray());

            if (walletManager.isConnect()) {
                credentialPrimaryPublicKeyMdl = walletManager.getCredentialPrimaryPublicKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_MDL);
                credentialPrimaryPublicKeyVl = walletManager.getCredentialPrimaryPublicKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_VL);
                credentialPrimaryPublicKeyVl2 = walletManager.getCredentialPrimaryPublicKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_VL2);
            } else {
                // nothing
            }

            credentialDefinitionMdl = new ZkpCredentialMetadataManager().createDefinition(ZkpSampleConstants.ISSUER_DID, schemaMdl, credentialPrimaryPublicKeyMdl);
            credentialDefinitionVl = new ZkpCredentialMetadataManager().createDefinition(ZkpSampleConstants.ISSUER_DID, schemaVl, credentialPrimaryPublicKeyVl);
            credentialDefinitionVl2 = new ZkpCredentialMetadataManager().createDefinition(ZkpSampleConstants.ISSUER_DID_VL2, schemaVl, credentialPrimaryPublicKeyVl2);

            System.out.println("credentialDefinition MDL: "  + getGsonPrettyPrinting().toJson(credentialDefinitionMdl));
            System.out.println("credentialDefinition VL: "  + getGsonPrettyPrinting().toJson(credentialDefinitionVl));
            System.out.println("credentialDefinition VL2: "  + getGsonPrettyPrinting().toJson(credentialDefinitionVl2));

            // todo : 블록체인 등록으로 변경해야함
            saveZKPData(credentialDefinitionMdl.getId(), credentialDefinitionMdl.toJson());
            saveZKPData(credentialDefinitionVl.getId(), credentialDefinitionVl.toJson());
            saveZKPData(credentialDefinitionVl2.getId(), credentialDefinitionVl2.toJson());

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void credentialSchema() {
        //todo : 스키마아이디와 일반 VC와 매칭
        //todo : 지금은 mdl / vl 각각 두개의 schema를 생성
        CredentialSchema credentialSchemaMdl = new ZkpCredentialMetadataManager().createSchema(ZkpSampleConstants.ISSUER_DID, ZkpSampleConstants.SCHEMA_NAME_MDL, ZkpSampleConstants.VERSION, ZkpSampleConstants.getAttributeListMdl(), ZkpSampleConstants.getAttributeTypeListMdl(), ZkpSampleConstants.TAG);
        CredentialSchema credentialSchemaVl = new ZkpCredentialMetadataManager().createSchema(ZkpSampleConstants.ISSUER_DID, ZkpSampleConstants.SCHEMA_NAME_VL, ZkpSampleConstants.VERSION, ZkpSampleConstants.getAttributeListVl(), ZkpSampleConstants.getAttributeTypeListVl(), ZkpSampleConstants.TAG);

        System.out.println("credentialSchema MDL : " + getGsonPrettyPrinting().toJson(credentialSchemaMdl));
        System.out.println("credentialSchema VL : " + getGsonPrettyPrinting().toJson(credentialSchemaVl));

        //todo : Credential Schema 메모리 저장 => 블록체인에 등록해야함
        saveZKPData(credentialSchemaMdl.getId(), GsonWrapper.getGson().toJson(credentialSchemaMdl));
        saveZKPData(credentialSchemaVl.getId(), GsonWrapper.getGson().toJson(credentialSchemaVl));

    }

    public static void createZkpKeys() {
        // mdl schema 조회
        String schemaStrMdl = loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL);
        CredentialSchema schemaMdl = new Gson().fromJson(schemaStrMdl, CredentialSchema.class);

        // vl schema 조회
        String schemaStrVl = loadZKPData(ZkpSampleConstants.SCHEMA_ID_VL);
        CredentialSchema schemaVl = new Gson().fromJson(schemaStrVl, CredentialSchema.class);

        // 월렛 생성 및 연결 및 키생성
        ZkpWalletManagerInterface walletManager = null;
        try {
            File file = new File(ZkpSampleConstants.WALLET_FILE_PATH);
            if (file.exists()) {
//                file.delete();
                return;
            }

            walletManager = ZkpWalletManagerFactory.getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            walletManager.create(ZkpSampleConstants.WALLET_FILE_PATH, ZkpSampleConstants.WALLET_PASSWORD.toCharArray(), ZkpWalletEncryptType.AES_256_CBC_PKCS5Padding);
            walletManager.connect(ZkpSampleConstants.WALLET_FILE_PATH, ZkpSampleConstants.WALLET_PASSWORD.toCharArray());

            if (walletManager.isConnect()) {
                if (walletManager.getZkpKeyElement(WALLET_TEST_KEY_ID_MDL) == null) {
                    walletManager.generateRandomZkpKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_MDL, schemaMdl.getAttrNames()); // mdl 키 -> MDL Schema
                    walletManager.generateRandomZkpKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_VL, schemaVl.getAttrNames()); //VL 키 -> VL Schema
                    walletManager.generateRandomZkpKey(ZkpSampleConstants.WALLET_TEST_KEY_ID_VL2, schemaVl.getAttrNames()); //VL2 키 -> VL Schema
                }

            } else {
                // nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static CredentialOffer getZkpSampleOffer() {
        try {
            ZkpWalletManagerInterface walletManager = ZkpWalletManagerFactory
                    .getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            walletManager.connect(ZkpSampleConstants.WALLET_FILE_PATH, ZkpSampleConstants.WALLET_PASSWORD.toCharArray());
            System.out.println("walletManager.getZkpKeyElement(WALLET_TEST_KEY_ID_MDL) = " + walletManager.getZkpKeyElement(WALLET_TEST_KEY_ID_MDL).getPublicKey());

            byte[] zkpKeyProof = walletManager.generateZkpKeyProof(ZkpSampleConstants.WALLET_TEST_KEY_ID_MDL);
            KeyCorrectnessProof keyCorrectnessProof = new Gson()
                    .fromJson(new String(zkpKeyProof), KeyCorrectnessProof.class);

            BigInteger issuerNonce = new BigInteger("936806592654063501937960");

            return new ZkpCredentialManager().createCredentialOffer(keyCorrectnessProof,
                    ZkpSampleConstants.SCHEMA_ID_MDL, ZkpSampleConstants.CRED_DEF_ID_MDL, issuerNonce);
        } catch (ZkpException e) {
            throw new RuntimeException(e);
        }
    }

    public static Credential getCredential(CredentialRequest credentialRequest, String vcId) {
        if(credentialRequest == null) {
            return null;
        }
        try {

            String schemaStr = loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL);
            LinkedHashMap<String, AttributeValue> credentialValue =
                    ZkpSampleConstants.genCredentialValueMdl(schemaStr, credentialRequest.getProverDid());

            CredentialValues credentialValues = new CredentialValues();
            credentialValues.setValues(credentialValue);

            String credentialDefinitionStr = loadZKPData(credentialRequest.getCredDefId());
            CredentialDefinition credentialDefinition = new Gson().fromJson(credentialDefinitionStr, CredentialDefinition.class);

            ZkpWalletManagerInterface walletManager = ZkpWalletManagerFactory
                    .getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            walletManager.connect(ZkpSampleConstants.WALLET_FILE_PATH, ZkpSampleConstants.WALLET_PASSWORD.toCharArray());


            byte[] zkpSignature = walletManager.generateZkpSignature(ZkpSampleConstants.WALLET_TEST_KEY_ID_MDL, credentialRequest, credentialValues);
            PrimaryCredentialSignature pCredSignature = new Gson().fromJson(new String(zkpSignature), PrimaryCredentialSignature.class);

            CredentialSignature credSignature = new CredentialSignature();
            credSignature.setPrimaryCredential(pCredSignature);

            // TODO
            BigInteger issuerNonce = new BigInteger("936806592654063501937960");

            byte[] zkpSignatureProof = walletManager.generateZkpSignatureProof(ZkpSampleConstants.WALLET_TEST_KEY_ID_MDL, credSignature, credentialRequest.getNonce());
            SignatureCorrectnessProof proof = new Gson().fromJson(new String(zkpSignatureProof), SignatureCorrectnessProof.class);


            return new ZkpCredentialManager().createCredential(credentialDefinition, credSignature, proof, credentialValue, credentialRequest, issuerNonce, vcId);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String loadZKPData(String key) {
        System.out.println("loadZKPData : " + key);
        StoreService storeService = StoreService.getInstance();
        String value = storeService.findById(key);
        return value;
    }

    // todo : 구현 시 블록체인에 schema와 definition 등록해야함
    public static boolean saveZKPData(String key, String value) {
        System.out.println("saveZKPData : " + key);
        StoreService storeService = StoreService.getInstance();
        storeService.save(key, value);
        return true;
    }

    public static String getCredentialSchema(ZkpLadgerRequestData request) throws ZkpException {
        //todo : blockchain 대신 메모리 저장값 사용
        System.out.println("getTableRow:"+ GsonWrapper.getGson().toJson(request));


        String schemaStr = loadZKPData(ZkpSampleConstants.SCHEMA_ID_MDL);
        String defStr = loadZKPData(ZkpSampleConstants.CRED_DEF_ID_MDL);
        //defnition
        CredentialSchema schema = GsonWrapper.getGson().fromJson(schemaStr, CredentialSchema.class);
        CredentialDefinition credentialDefinition = new Gson().fromJson(defStr, CredentialDefinition.class);

        System.out.println("credentialSchema MDL : " + schema.getId());
        System.out.println("credentialDefinition MDL : " + credentialDefinition);



        ZkpLadgerResponseData ladgerResponseData = new ZkpLadgerResponseData();
        ladgerResponseData.setMore(false);
        ladgerResponseData.setNext_key("");

        if (request.getTable().equals("schema")) {

            List<ZkpSchemaRows> list = new ArrayList<>();
            ZkpSchemaRows row = new ZkpSchemaRows();
            row.setSchema_desc("");
            row.setSchema_id(schema.getId());
            row.setSchema_id_hash(request.getLower_bound());

            List<String> schemaList = schema.getAttrNames();
            schemaList.remove("master_secret");
            schema.setAttrNames(schemaList);

            row.setSchema_value(GsonWrapper.getGson().toJson(schema));
            row.setP_key(1);
            list.add(row);
            ladgerResponseData.setRows(list);
        }
        else if (request.getTable().equals("creddef")) {
            List<ZkpCredentionDefRows> list = new ArrayList<>();
            ZkpCredentionDefRows row = new ZkpCredentionDefRows();
            row.setCreddef_desc("");
            row.setCreddef_id(credentialDefinition.getId());
            row.setCreddef_id_hash(request.getLower_bound());
            row.setCreddef_value(defStr);
            row.setP_key(1);
            list.add(row);
            ladgerResponseData.setRows(list);
        }

        System.out.println("ladgerResponseData :"+ getGsonPrettyPrinting().toJson(ladgerResponseData));
        return GsonWrapper.getGson().toJson(ladgerResponseData);

    }
}
