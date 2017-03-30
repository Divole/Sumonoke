package com.example.dovile.sumonoke.cards;


public class Hero implements Card {

    private String ability;
    private String name; //: 'Swamp Spirit',
    private String imgName; //: 'swamp_spirit'
    private int hp; //: 350,
    private int atk; //: 100,
    private int def; //: 50,
    private int heal; //: 0,
    private int regen; //: 7,
    private int range; //: 53
    private  int id;

    public Hero(int hp, int range, String name, String ability, String imgName) {
        this.hp = hp;
        this.range = range;
        this.name = name;
        this.ability = ability;
        this.imgName = imgName;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
