//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget;

public class Position {
    int x;
    int y;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position p) {
        this.x = p.x();
        this.y = p.y();
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(Position p) {
        this.x = p.x();
        this.y = p.y();
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public void offset(int offsetX, int offsetY) {
        this.x += offsetX;
        this.y += offsetY;
    }
}
