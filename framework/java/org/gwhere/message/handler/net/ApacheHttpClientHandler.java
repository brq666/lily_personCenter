package org.gwhere.message.handler.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by jiangtao on 2015/7/28.
 */
public class ApacheHttpClientHandler extends HttpHandler<HttpRequestMessage, Object, Object> {

    public ApacheHttpClientHandler() {
        init();
    }

    private final static int CONN_MAX_TOTAL = 200;

    private final static int CONN_MAX_PER_ROUTE = 200;

    private final static int socketTimeout = 10000;

    private final static int connectTimeout = 30000;

    private PoolingHttpClientConnectionManager connectionManager;

    private CloseableHttpClient httpClient;

    private RequestConfig requestConfig;

    SSLConnectionSocketFactory getSSLSocketFactory() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        return null;
    }

    public CredentialsProvider getProxyCredentialsProvider() {
        return null;
    }

    public HttpHost getProxy() {
        return null;
    }


    private void init() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONN_MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(CONN_MAX_PER_ROUTE);
        SSLConnectionSocketFactory sslConnectionSocketFactory;
        HttpHost proxy;
        CredentialsProvider credentialsProvider;
        HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        try {
            sslConnectionSocketFactory = getSSLSocketFactory();
            if (sslConnectionSocketFactory != null) {
                clientBuilder.setSSLSocketFactory(sslConnectionSocketFactory).build();
            }
        } catch (Exception e) {
            logger.error("初始化证书失败", e);
        }
        proxy = getProxy();
        credentialsProvider = getProxyCredentialsProvider();
        if (proxy != null) {
            clientBuilder.setProxy(proxy);
            if (credentialsProvider != null) {
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                        .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
            }
        }
        httpClient = clientBuilder.build();
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    private ThreadLocal<RequestMsgDesc> messageDesc = new ThreadLocal<>();

    @Override
    protected boolean isDoInput() {
        return messageDesc.get().isDoInput();
    }

    protected String getRequestMethod() {
        return messageDesc.get().requestMethod();
    }

    @Override
    protected boolean isDoOutput() {
        return messageDesc.get().isDoOutput();
    }

    protected Class getRequestDataType() {
        return messageDesc.get().requestDataType();
    }

    protected Class getResponseDataType() {
        return messageDesc.get().responseDataType();
    }

    protected boolean checkHttpMessage(HttpRequestMessage requestMessage) {
        messageDesc.set(requestMessage.getRequestMsgDesc());
        if (isDoOutput()) {
            Class requestDataType = requestMessage.getMessageData().getClass();
            return getRequestDataType().isAssignableFrom(requestDataType);
        }
        return true;
    }

    @Override
    protected void doOutput(OutputStream outputStream, Object outputData) {

    }

    @Override
    public Object handlerMessage(HttpRequestMessage httpRequestMessage) {
        if (!checkHttpMessage(httpRequestMessage)) {
            throw new HttpConnectException("checkHttpMessage error");
        }
        HttpRequestBase httpRequest = null;
        Object responseData = null;
        Object requestData = httpRequestMessage.getMessageData();
        HttpHandlerListener httpHandlerListener = httpRequestMessage.getHttpConnectListener();
        Object messageSource = httpRequestMessage.getSource();
        try {
            if (httpHandlerListener != null) {
                httpHandlerListener.beforeDoHandler(messageSource);
            }
            if ("POST".equals(getRequestMethod().toUpperCase())) {
                httpRequest = new HttpPost(httpRequestMessage.getUrl());
            } else {
                httpRequest = new HttpGet(httpRequestMessage.getUrl());
            }
            if (!useCaches()) {
                httpRequest.addHeader("Cache-Control", "no-cache");
            }
            httpRequest.setConfig(requestConfig);
            if (httpRequest instanceof HttpPost && isDoOutput()) {
                if (httpHandlerListener != null) {
                    httpHandlerListener.beforeDoOutput(requestData, messageSource);
                }
                HttpEntity httpEntity;
                if (requestData instanceof FormData) {
                    httpEntity = doFormPostOutput((FormData) requestData);
                } else {
                    httpEntity = doTextPostOutput(requestData);
                }
                ((HttpPost) httpRequest).setEntity(httpEntity);
            }
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (isDoInput()) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                try {
                    responseData = doInput(inputStream);
                    if (httpHandlerListener != null) {
                        httpHandlerListener.afterDoInput(responseData, messageSource);
                    }
                } finally {
                    inputStream.close();
                }
            }
            if (httpHandlerListener != null) {
                httpHandlerListener.afterDoHandler(messageSource);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("handlerMessage error", e);
            }
            if (httpHandlerListener != null) {
                httpHandlerListener.onException(e, messageSource);
            }
        } finally {
            if (httpRequest != null)
                httpRequest.releaseConnection();
        }
        return responseData;
    }

    protected boolean useCaches() {
        return false;
    }

    @Override
    protected Object doInput(InputStream inputStream) {
        if (messageConverter.canRead(getResponseDataType())) {
            return messageConverter.read(getResponseDataType(), inputStream);
        } else {
            throw new HttpConnectException("Unable read response data with class " + getResponseDataType());
        }
    }

    public HttpEntity doFormPostOutput(FormData formDataMessage) {
        StringEntity reqEntity = new StringEntity(formDataMessage.toString(), "UTF-8");
        reqEntity.setContentType("application/x-www-form-urlencoded");
        return reqEntity;
    }

    public HttpEntity doTextPostOutput(Object httpRequestMessage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (messageConverter.canWrite(httpRequestMessage.getClass())) {
            messageConverter.write(httpRequestMessage, outputStream);
        } else {
            throw new HttpConnectException("Unable write request data with class " + httpRequestMessage.getClass());
        }
        try {
            return new StringEntity(outputStream.toString("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new HttpConnectException("Unsupported Encoding UTF-8", e);
        }


    }
}
