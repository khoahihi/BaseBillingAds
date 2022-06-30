package com.mmgsoft.modules.libs.data.model.event;

/**
 * Created by khoa_nd on 5/15/2019.
 * SavvyCom
 * dangkhoait1989@gmail.com
 */
public class UpdateProfileSuccessEvent {
	public UpdateProfileSuccessEvent(String avatar) {
		this.avatar = avatar;
	}

	public String avatar;
}
