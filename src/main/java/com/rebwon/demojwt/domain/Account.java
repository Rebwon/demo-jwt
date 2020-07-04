package com.rebwon.demojwt.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts", uniqueConstraints = {
	@UniqueConstraint(columnNames = "email")
})
@NoArgsConstructor @Builder
@Getter @Setter @DynamicUpdate @DynamicInsert
public class Account {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Email
	@Column(nullable = false)
	private String email;
	private String imageUrl;
	@Column(nullable = false)
	private Boolean emailVerified = false;
	@JsonIgnore
	private String password;
	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	private String providerId;
}
