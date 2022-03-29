package com.delgo.api.security.services;

import com.delgo.api.domain.user.User;
import com.delgo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("PrincipalDetailsService : 진입 String 값" + email);
		User user = userRepository.findByEmail(email);
		if(user == null)
			return null;

		return new PrincipalDetails(user);
	}
}
