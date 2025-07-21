package com.adhitya.paymgmt;

import com.adhitya.paymgmt.config.AppConfig;

public class Main {
  public static void main(String[] args) {
    AppConfig config = new AppConfig();
    config.getMainMenu().showGeneralMenu();
  }
}