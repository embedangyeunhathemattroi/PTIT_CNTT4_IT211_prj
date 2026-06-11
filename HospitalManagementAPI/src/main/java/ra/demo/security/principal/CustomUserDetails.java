package ra.demo.security.principal;

// Interface biểu diễn quyền
import org.springframework.security.core.GrantedAuthority;
// Tạo role cho user
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// Interface chuẩn của Spring Security
import org.springframework.security.core.userdetails.UserDetails;
// Entity User
import ra.demo.model.entity.User;

import java.util.Collection;
import java.util.List;

// Custom UserDetails chứa thông tin user đăng nhập
public record CustomUserDetails(User user) implements UserDetails {

 // Trả về danh sách quyền của user
 public Collection<? extends GrantedAuthority> getAuthorities() {

  // Tạo ROLE_ADMIN hoặc ROLE_USER
  return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
 }

 // Lấy password của user
 public String getPassword() {
  return user.getPassword();
 }

 // Lấy username của user
 public String getUsername() {
  return user.getUsername();
 }

 // Kiểm tra tài khoản còn hiệu lực
 public boolean isAccountNonExpired() {
  return true;
 }

 // Kiểm tra tài khoản có bị khóa không
 public boolean isAccountNonLocked() {
  return user.isActive();
 }

 // Kiểm tra mật khẩu còn hiệu lực
 public boolean isCredentialsNonExpired() {
  return true;
 }

 // Kiểm tra tài khoản đã kích hoạt chưa
 public boolean isEnabled() {
  return user.isActive();
 }
}