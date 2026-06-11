package ra.demo.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ra.demo.model.entity.User;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(User user) implements UserDetails {

 public Collection<? extends GrantedAuthority> getAuthorities() {
  return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
 }

 public String getPassword() {
  return user.getPassword();
 }

 public String getUsername() {
  return user.getUsername();
 }

 public boolean isAccountNonExpired() {
  return true;
 }

 public boolean isAccountNonLocked() {
  return user.isActive();
 }

 public boolean isCredentialsNonExpired() {
  return true;
 }

 public boolean isEnabled() {
  return user.isActive();
 }
}
