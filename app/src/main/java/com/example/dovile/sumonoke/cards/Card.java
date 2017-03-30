package com.example.dovile.sumonoke.cards;


public interface Card {
    String getName();

    String getImgName();

    void setImgName(String imgName);

    void setName(String name);

    int getHp();

    void setHp(int hp);

    int getAtk();

    void setAtk(int atk);

    int getDef();

    void setDef(int def);

    int getHeal();

    void setHeal(int heal);

    int getRegen();

    void setRegen(int regen);

    int getRange();

    void setRange(int range);

    int getId();

    void setId(int id);
}
