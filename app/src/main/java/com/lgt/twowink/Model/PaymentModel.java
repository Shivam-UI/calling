package com.lgt.twowink.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentModel {

    @SerializedName("payment_request")
    @Expose
    private PaymentRequest paymentRequest;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public PaymentRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(PaymentRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public class PaymentRequest{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("buyer_name")
        @Expose
        private String buyerName;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("purpose")
        @Expose
        private String purpose;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("send_sms")
        @Expose
        private Boolean sendSms;
        @SerializedName("send_email")
        @Expose
        private Boolean sendEmail;
        @SerializedName("sms_status")
        @Expose
        private String smsStatus;
        @SerializedName("email_status")
        @Expose
        private String emailStatus;
        @SerializedName("shorturl")
        @Expose
        private Object shorturl;
        @SerializedName("longurl")
        @Expose
        private String longurl;
        @SerializedName("redirect_url")
        @Expose
        private String redirectUrl;
        @SerializedName("webhook")
        @Expose
        private String webhook;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("modified_at")
        @Expose
        private String modifiedAt;
        @SerializedName("allow_repeated_payments")
        @Expose
        private Boolean allowRepeatedPayments;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getSendSms() {
            return sendSms;
        }

        public void setSendSms(Boolean sendSms) {
            this.sendSms = sendSms;
        }

        public Boolean getSendEmail() {
            return sendEmail;
        }

        public void setSendEmail(Boolean sendEmail) {
            this.sendEmail = sendEmail;
        }

        public String getSmsStatus() {
            return smsStatus;
        }

        public void setSmsStatus(String smsStatus) {
            this.smsStatus = smsStatus;
        }

        public String getEmailStatus() {
            return emailStatus;
        }

        public void setEmailStatus(String emailStatus) {
            this.emailStatus = emailStatus;
        }

        public Object getShorturl() {
            return shorturl;
        }

        public void setShorturl(Object shorturl) {
            this.shorturl = shorturl;
        }

        public String getLongurl() {
            return longurl;
        }

        public void setLongurl(String longurl) {
            this.longurl = longurl;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getWebhook() {
            return webhook;
        }

        public void setWebhook(String webhook) {
            this.webhook = webhook;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getModifiedAt() {
            return modifiedAt;
        }

        public void setModifiedAt(String modifiedAt) {
            this.modifiedAt = modifiedAt;
        }

        public Boolean getAllowRepeatedPayments() {
            return allowRepeatedPayments;
        }

        public void setAllowRepeatedPayments(Boolean allowRepeatedPayments) {
            this.allowRepeatedPayments = allowRepeatedPayments;
        }
    }

}
