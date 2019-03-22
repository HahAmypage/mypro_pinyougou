package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class UserDetailServiceImpl implements UserDetailsService{

//    手动注入sellerService ,不能用注解注入
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller seller = sellerService.findOne(username);
        if (seller!=null&& "1".equals(seller.getStatus().trim())){
            Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_SELLER");
            grantedAuthoritySet.add(authority);
            User user = new User(username,seller.getPassword(),grantedAuthoritySet);
            return user;
        }
        return null;
    }
}
