package me.noahandrews.biscuitcaseapp;

public enum Section {
    MENU("menu"), STORE("store");

    final String sectionName;

    Section(String name) {
        this.sectionName = name;
    }

    public static Section forString(String name){
        for(Section section: Section.values()){
            if(section.sectionName.equalsIgnoreCase(name)){
                return section;
            }
        }
        return null;
    }
}
