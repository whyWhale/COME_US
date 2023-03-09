package com.platform.order.common.supperentity;

import javax.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class FileBaseEntity {
	protected String originName;
	protected String fileName;
	protected String path;
	protected String extension;
	protected Long size;

	public String generateFullFileName() {
		return this.getFileName() + "." + this.getExtension();
	}
}
