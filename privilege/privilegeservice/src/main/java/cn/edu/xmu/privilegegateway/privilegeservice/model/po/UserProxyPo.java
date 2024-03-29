package cn.edu.xmu.privilegegateway.privilegeservice.model.po;

import java.time.LocalDateTime;

public class UserProxyPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.user_id
     *
     * @mbg.generated
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.proxy_user_id
     *
     * @mbg.generated
     */
    private Long proxyUserId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.begin_date
     *
     * @mbg.generated
     */
    private LocalDateTime beginDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.end_date
     *
     * @mbg.generated
     */
    private LocalDateTime endDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.signature
     *
     * @mbg.generated
     */
    private String signature;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.valid
     *
     * @mbg.generated
     */
    private Byte valid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.depart_id
     *
     * @mbg.generated
     */
    private Long departId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.creator_id
     *
     * @mbg.generated
     */
    private Long creatorId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.gmt_create
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.modifier_id
     *
     * @mbg.generated
     */
    private Long modifierId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.creator_name
     *
     * @mbg.generated
     */
    private String creatorName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.modifier_name
     *
     * @mbg.generated
     */
    private String modifierName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.user_name
     *
     * @mbg.generated
     */
    private String userName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column auth_user_proxy.proxy_user_name
     *
     * @mbg.generated
     */
    private String proxyUserName;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.id
     *
     * @return the value of auth_user_proxy.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.id
     *
     * @param id the value for auth_user_proxy.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.user_id
     *
     * @return the value of auth_user_proxy.user_id
     *
     * @mbg.generated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.user_id
     *
     * @param userId the value for auth_user_proxy.user_id
     *
     * @mbg.generated
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.proxy_user_id
     *
     * @return the value of auth_user_proxy.proxy_user_id
     *
     * @mbg.generated
     */
    public Long getProxyUserId() {
        return proxyUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.proxy_user_id
     *
     * @param proxyUserId the value for auth_user_proxy.proxy_user_id
     *
     * @mbg.generated
     */
    public void setProxyUserId(Long proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.begin_date
     *
     * @return the value of auth_user_proxy.begin_date
     *
     * @mbg.generated
     */
    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.begin_date
     *
     * @param beginDate the value for auth_user_proxy.begin_date
     *
     * @mbg.generated
     */
    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.end_date
     *
     * @return the value of auth_user_proxy.end_date
     *
     * @mbg.generated
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.end_date
     *
     * @param endDate the value for auth_user_proxy.end_date
     *
     * @mbg.generated
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.signature
     *
     * @return the value of auth_user_proxy.signature
     *
     * @mbg.generated
     */
    public String getSignature() {
        return signature;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.signature
     *
     * @param signature the value for auth_user_proxy.signature
     *
     * @mbg.generated
     */
    public void setSignature(String signature) {
        this.signature = signature == null ? null : signature.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.valid
     *
     * @return the value of auth_user_proxy.valid
     *
     * @mbg.generated
     */
    public Byte getValid() {
        return valid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.valid
     *
     * @param valid the value for auth_user_proxy.valid
     *
     * @mbg.generated
     */
    public void setValid(Byte valid) {
        this.valid = valid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.depart_id
     *
     * @return the value of auth_user_proxy.depart_id
     *
     * @mbg.generated
     */
    public Long getDepartId() {
        return departId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.depart_id
     *
     * @param departId the value for auth_user_proxy.depart_id
     *
     * @mbg.generated
     */
    public void setDepartId(Long departId) {
        this.departId = departId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.creator_id
     *
     * @return the value of auth_user_proxy.creator_id
     *
     * @mbg.generated
     */
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.creator_id
     *
     * @param creatorId the value for auth_user_proxy.creator_id
     *
     * @mbg.generated
     */
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.gmt_create
     *
     * @return the value of auth_user_proxy.gmt_create
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.gmt_create
     *
     * @param gmtCreate the value for auth_user_proxy.gmt_create
     *
     * @mbg.generated
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.modifier_id
     *
     * @return the value of auth_user_proxy.modifier_id
     *
     * @mbg.generated
     */
    public Long getModifierId() {
        return modifierId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.modifier_id
     *
     * @param modifierId the value for auth_user_proxy.modifier_id
     *
     * @mbg.generated
     */
    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.gmt_modified
     *
     * @return the value of auth_user_proxy.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.gmt_modified
     *
     * @param gmtModified the value for auth_user_proxy.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.creator_name
     *
     * @return the value of auth_user_proxy.creator_name
     *
     * @mbg.generated
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.creator_name
     *
     * @param creatorName the value for auth_user_proxy.creator_name
     *
     * @mbg.generated
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName == null ? null : creatorName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.modifier_name
     *
     * @return the value of auth_user_proxy.modifier_name
     *
     * @mbg.generated
     */
    public String getModifierName() {
        return modifierName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.modifier_name
     *
     * @param modifierName the value for auth_user_proxy.modifier_name
     *
     * @mbg.generated
     */
    public void setModifierName(String modifierName) {
        this.modifierName = modifierName == null ? null : modifierName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.user_name
     *
     * @return the value of auth_user_proxy.user_name
     *
     * @mbg.generated
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.user_name
     *
     * @param userName the value for auth_user_proxy.user_name
     *
     * @mbg.generated
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column auth_user_proxy.proxy_user_name
     *
     * @return the value of auth_user_proxy.proxy_user_name
     *
     * @mbg.generated
     */
    public String getProxyUserName() {
        return proxyUserName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column auth_user_proxy.proxy_user_name
     *
     * @param proxyUserName the value for auth_user_proxy.proxy_user_name
     *
     * @mbg.generated
     */
    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName == null ? null : proxyUserName.trim();
    }
}