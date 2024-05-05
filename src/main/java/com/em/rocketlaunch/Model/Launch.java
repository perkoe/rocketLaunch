package com.em.rocketlaunch.Model;

import java.util.Objects;


public class Launch {
    private String id;
    private String name;
    private String net;  // Net stands for "No Earlier Than", the scheduled date/time of the launch
    public Launch() {}
    public Launch(String name, String net, String id) {
        this.name = name;
        this.net = net;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Launch launch = (Launch) o;
        return Objects.equals(id, launch.id) &&
                Objects.equals(name, launch.name) &&
                Objects.equals(net, launch.net);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, net);
    }

    @Override
    public String toString() {
        return "Launch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", net='" + net + '\'' +
                '}';
    }
}
