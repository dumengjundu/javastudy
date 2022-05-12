package com.qdc.demoeurekaauth_server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;


@Component
@Configuration
public class Oauth2AuthoriztionServerConfigureation extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DruidDataSource druidDataSource;

    /**
     * @param security
     * @throws Exception
     * AuthorizationServerSecurityConfigurer :配置合牌端点的安全约束
     * //设置了只有ROLE_TRUSTED_CLIENT权限的客户端才可以进行安全验证。
     * //所以需要检查oauth_client_details中客户端记录的authorities字段设置为
     * ROLE_TRUSTED_CLIENT
     *
     */

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        super.configure(security);
        System.out.println("aaaa");
        security.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    /**
     *
     * @param clients  客户端服务管理方法
     * 指定用数据库管理客户端应用，从dataSource配置的数据源中读取客户端数据
     * 客户端数据都保存在表oauth_client_details中
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        super.configure(clients);
        clients.withClientDetails(new JdbcClientDetailsService(druidDataSource));
        System.out.println("bbbb");
    }

    /**
     *
     * @param endpoints  配置授权以及
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        super.configure(endpoints);
        System.out.println("cccc");
        //用户信息查询服务
        endpoints.userDetailsService(userDetailsService);
        //数据库管理access_token和refresh_token
        TokenStore tokenStore = new JdbcTokenStore(druidDataSource);
        //endpointS . tokenStore (tokenStore);
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(new JdbcClientDetailsService(druidDataSource));
        tokenServices.setAccessTokenValiditySeconds(38000);
        //tokenServices.setRefreshTokenValiditySeconds(180);
        endpoints.tokenServices(tokenServices);
        //数据库管理授权码
        endpoints.authorizationCodeServices(new JdbcAuthorizationCodeServices(druidDataSource));
        //数据库管理授权信息
        ApprovalStore approvalStore = new JdbcApprovalStore(druidDataSource);
        endpoints.approvalStore(approvalStore);
    }
}
