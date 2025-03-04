package com.trego.service;

import com.trego.dto.MainDTO;

public interface IMainService {
 public MainDTO loadAll(String address,long lat, long lng);

}
