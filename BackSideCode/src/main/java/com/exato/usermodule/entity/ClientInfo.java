package com.exato.usermodule.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "clientinfo",indexes = {@Index(name = "idx_clientName", columnList="client_name",unique=true)})
public class ClientInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clientId;

	@Column(name = "spoc_name")
	private String spocName;
	
	@Column(name = "client_name" )
	private String clientName;

	@Column(name = "client_email")
	private String email;

	@Column(name = "client_password")
	private String password;

	@Column(name = "client_phonenumber")
	private String phonenumber;

	@Column(name = "Client_bussinessnumber")
	private String bussinessnumber;

	@Column(name = "Client_address")
	private String address;

	@Column(name = "active")
	private boolean isActive;

	private String otpNumber;
	
	private String businessUnit;
    private String processUnit;
    private String team;
    private String groupid;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "client_roles", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> assignedRoles = new HashSet<>();

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date updatedDate;

}
