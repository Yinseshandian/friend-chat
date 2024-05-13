package com.li.chat.config;



import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.Page;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.li.chat.common.utils.ResultData;
import io.swagger.annotations.ApiModelProperty;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.*;
import springfox.documentation.service.Parameter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;

/**
 *
 * @author malaka
 */
//
// @Component
// @Order   //plugin加载顺序，默认是最后加载
public class MapApiReader implements OperationModelsProviderPlugin , OperationBuilderPlugin  {

    private final static String basePackage = "com.li.chat.common.utils.";  //动态生成的Class名

    @Autowired
    private TypeResolver typeResolver;



    @Override
    public void apply(RequestMappingContext context) {
        if (context.getReturnType().isInstanceOf(ResultData.class)) {
            // // // 生成ResultData类
            ResolvedType rt = typeResolver.resolve(createRefModelResultData());
            context.getDocumentationContext()
                    .getAdditionalModels()
                    .add(rt);
            System.out.println(context.operationModelsBuilder()
                    .addReturn(rt).getType());
            // ResponseMessage responseMessage = new ResponseMessageBuilder()
            //         .code(200).responseModel(new ModelRef(rt.getTypeName()))
            //         .build();
            //
            // HashSet<ResponseMessage> responseMessages = new HashSet<>();
            // responseMessages.add(responseMessage);

            // UserInfoVo userInfoVo = UserInfoVo.builder().build();
            // context.getDocumentationContext()
            //         .getAdditionalModels()
            //         .add(typeResolver.resolve(userInfoVo.getClass()));
            // context.operationBuilder().responseModel()
        }

    }

    private Class createRefModelResultData() {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(basePackage + "ResultData" + new Random().nextInt());
        try {
            ctClass.addField(createField("code",Integer.class,"","返回码",ctClass));
            ctClass.addField(createField("msg",String.class,"","返回提示",ctClass));
            ctClass.addField(createField("success",Boolean.class,"","是否成功",ctClass));
            // ctClass.addField(createField("data",jsonR.dataType(),jsonR.responseContainer(),jsonR.description(),ctClass));
            return ctClass.toClass();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private CtField createField(String key, Class<?> dataType, String responseContainer,
                                String description,
                                CtClass ctClass) throws Exception {
        CtClass fieldClass = getFieldType(dataType, responseContainer);
        CtField ctField = new CtField(fieldClass, key, ctClass);
        if(StringUtils.isNotBlank(responseContainer)){
            getGenericSignature(ctField,dataType,responseContainer);
        }
        ctField.setModifiers(Modifier.PUBLIC);
        ConstPool constPool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation ann = new Annotation("io.swagger.annotations.ApiModelProperty", constPool);
        ann.addMemberValue("value", new StringMemberValue(description, constPool));
     /*   if(ctField.getType().subclassOf(ClassPool.getDefault().get(String.class.getName()))){
            ann.addMemberValue("example", new StringMemberValue(example, ClassPool.getDefault().get(String.class.getName()).getClassFile().getConstPool()));
        }
        if(ctField.getType().subclassOf(ClassPool.getDefault().get(Integer.class.getName()))){
            ann.addMemberValue("example", new IntegerMemberValue(Integer.parseInt(example), ClassPool.getDefault().get(Integer.class.getName()).getClassFile().getConstPool()));
        }
        if(ctField.getType().subclassOf(ClassPool.getDefault().get(Boolean.class.getName()))){
            ann.addMemberValue("example", new BooleanMemberValue(Boolean.parseBoolean(example), ClassPool.getDefault().get(Boolean.class.getName()).getClassFile().getConstPool()));
        }*/
        attr.addAnnotation(ann);
        ctField.getFieldInfo().addAttribute(attr);

        return ctField;
    }

    /**
     * 生成返回对象的属性class
     * @param classObj
     * @param responseContainer
     * @return
     * @throws Exception
     */
    private CtClass getFieldType(Class<?> classObj,String responseContainer) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        if ("List".compareToIgnoreCase(responseContainer) == 0) {
            return classPool.get(List.class.getCanonicalName());
        } else if ("Set".compareToIgnoreCase(responseContainer) == 0) {
            return classPool.get(Set.class.getCanonicalName());
        } else if ("Page".compareToIgnoreCase(responseContainer) == 0) {
            return classPool.get(Page.class.getCanonicalName());
        }
        return classPool.get(classObj.getName());
    }

    /**
     *  javasist对CtField的泛型类型添加泛型的类声明
     * @param ctField
     * @param relatedClass
     * @param responseContainer
     * @return
     * @throws BadBytecode
     */
    private CtField getGenericSignature(CtField ctField, Class<?> relatedClass,String responseContainer) throws BadBytecode {
        String fieldSignature = "";
        if ("List".compareToIgnoreCase(responseContainer) == 0) {
            fieldSignature = "L" + List.class.getCanonicalName().replace(".", "/") + "<L" + relatedClass.getCanonicalName().replace(".", "/") + ";>;";
        } else if ("Set".compareToIgnoreCase(responseContainer) == 0) {
            fieldSignature = "L" + Set.class.getCanonicalName().replace(".", "/") + "<L" + relatedClass.getCanonicalName().replace(".", "/") + ";>;";
        } else if ("Page".compareToIgnoreCase(responseContainer) == 0) {
            fieldSignature = "L" + Page.class.getCanonicalName().replace(".", "/") + "<L" + relatedClass.getCanonicalName().replace(".", "/") + ";>;";
        }else {
            return ctField;
        }
        ctField.setGenericSignature(SignatureAttribute.toClassSignature(fieldSignature).encode());
        return ctField;
    }


    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext operationContext) {
        List<Parameter> newParameters = new ArrayList<>();
        OperationBuilder operationBuilder = operationContext.operationBuilder();
        try {
            Field parameterField = operationBuilder.getClass().getDeclaredField("parameters");
            parameterField.setAccessible(true);
            List<Parameter> parameters = (List<Parameter>) parameterField.get(operationBuilder);
            for (int i = 0; i < parameters.size(); i++) {
                Parameter parameter = parameters.get(i);
                String name = parameter.getName();
                System.out.println(name);
                // reqReplaceMap用来替代参数接入
                if ("reqReplaceMap".equals(name)) {
                    continue;
                }

                // reqObj展开解析
                // if (Constants.REQ_OBJ.equals(name)) {
                if (false) {
                    ResolvedType resolvedType = parameter.getType().get();
                    Class<?> aClass = Class.forName(resolvedType.getTypeName());
                    Field[] declaredFields = aClass.getDeclaredFields();
                    // 所有属性，除了serialVersionUID
                    for (int i1 = 0; i1 < declaredFields.length; i1++) {
                        Field declaredField = declaredFields[i1];
                        String fieldName = declaredField.getName();
                        if (!"serialVersionUID".equals(fieldName)) {
                            String description = fieldName;
                            ApiModelProperty apiModelProperty = declaredField.getAnnotation(ApiModelProperty.class);
                            // 默认字段名作为描述
                            if (apiModelProperty != null) {
                                description = apiModelProperty.value();
                            }
                            // Parameter query = new Parameter(fieldName,
                            //         description,
                            //         "",
                            //         false,
                            //         true,
                            //         new ModelRef("string"),
                            //         Optional.of(typeResolver.resolve(declaredField.getType())),
                            //         null,
                            //         "query",
                            //         "",
                            //         false,
                            //         "",
                            //         "",
                            //         new ArrayList<>());
                            // newParameters.add(query);
                        }
                    }
                    continue;
                }
                newParameters.add(parameter);
            }
            if (!CollectionUtil.isEmpty(newParameters)) {
                // 反射替换新的参数
                Field buildParameterField = operationBuilder.getClass().getDeclaredField("parameters");
                buildParameterField.setAccessible(true);
                buildParameterField.set(operationBuilder, newParameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}