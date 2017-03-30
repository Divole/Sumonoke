package com.example.dovile.sumonoke.cards;

public class Demon implements Card, Cloneable{

    private String name; //: 'Swamp Spirit',
    private String imgName; //: 'swamp_spirit'
    private int hp; //: 350,
    private int atk; //: 100,
    private int def; //: 50,
    private int heal; //: 0,
    private int regen; //: 7,
    private int range; //: 53
    private  int id;

    public Demon(int id, String name, String imgName, int hp, int atk, int def, int heal, int regen, int range){
        this.id = id;
        this.name = name;
        this.imgName = imgName;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.heal = heal;
        this.regen = regen;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getHeal() {
        return heal;
    }

    public void setHeal(int heal) {
        this.heal = heal;
    }

    public int getRegen() {
        return regen;
    }

    public void setRegen(int regen) {
        this.regen = regen;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Demon clone() throws CloneNotSupportedException {
        return (Demon) super.clone();
    }
}
