package org.freedom.aia;

import org.freedom.util.FileUtil;

import java.io.*;
import java.util.*;

/**
 * Created by chaolin on 2017/4/20.
 * 聚类算法基类
 */
public abstract class Clustering {
    // 聚类的项目集
    protected List<AiaProject> aiaProjects;
    // 项目集无效的项目
    protected List<AiaProject> badAia = new ArrayList<>();
    // 项目集的距离矩阵
    protected Map<AiaProject, Map<AiaProject, Double>> disMap;

    private CalculateDistance calculator;
    /**
     *
     * @param aiaProjects
     *            聚类项目list
     */
    public Clustering(List<AiaProject> aiaProjects) {
        super();
        this.aiaProjects = aiaProjects;
        createDisMap();
    }

    public Clustering(List<AiaProject> aiaProjects, double[][] disMetrix){
        super();
        this.aiaProjects = aiaProjects;
        disMap=new HashMap<>();
        for(int i=0;i<aiaProjects.size();i++){
            Map<AiaProject,Double> dis=new HashMap<>();
            for(int j=i+1;j<aiaProjects.size();j++){
                dis.put(aiaProjects.get(j),disMetrix[i][j]);
            }
            disMap.put(aiaProjects.get(i),dis);
        }
    }
    /**
     *
     * @param path
     *            聚类的项目所在路径
     */
    public Clustering(String path) {
        super();
        File dir = new File(path);
        if (dir.isDirectory()) {
            List<File> files = FileUtil.findAiaFile(dir);
            aiaProjects = new ArrayList<>();
            System.out.println("start parse");
            long start= System.currentTimeMillis();
            for (int i = 0; i < files.size(); i++) {
                AiaProject aia=new AiaProject(files.get(i));
                aiaProjects.add(aia);
            }
            System.out.println("end parse after "+(System.currentTimeMillis()-start)+" ms");
        } else if (path.toLowerCase().endsWith(".zip")) {
            try {
                aiaProjects = FileUtil.UnzipToAiaProjects(new FileInputStream(dir));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        createDisMap();
    }

    /**
     *
     * @param path
     *            聚类的项目所在路径
     */
    public Clustering(String path, String titleFile) {
        super();
        File dir = new File(path);
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(titleFile));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (dir.isDirectory()) {
            List<File> files = FileUtil.findAiaFile(dir);
            aiaProjects = new ArrayList<>();
            System.out.println("start parse");
            long start= System.currentTimeMillis();
            for (int i = 0; i < files.size(); i++) {
                AiaProject aia=new AiaProject(files.get(i));
                aia.setTitle(prop.getProperty(aia.getName()));
                aiaProjects.add(aia);
            }
            System.out.println("end parse after "+(System.currentTimeMillis()-start)+" ms");
        } else if (path.toLowerCase().endsWith(".zip")) {
            try {
                aiaProjects = FileUtil.UnzipToAiaProjects(new FileInputStream(dir));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        createDisMap();
    }

    /**
     * 包含聚类项目的ZIP流
     *
     * @param file
     */
    public Clustering(InputStream file) {
        aiaProjects = FileUtil.UnzipToAiaProjects(file);
        createDisMap();
    }

    /**
     * 求取距离矩阵
     */
    private void createDisMap() {
        System.out.println("start create distance map");
        long start = System.currentTimeMillis();
        calculator = new CalculateDistance(aiaProjects);
        disMap = calculator.calculateAllDistanceToMap();
        System.out.println("end create distance map after " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 获取Aia之间的距离
     *
     * @param aiaA
     * @param aiaB
     * @return
     */
    public double getAiaDistance(AiaProject aiaA, AiaProject aiaB) {
        Double dis = disMap.get(aiaA).get(aiaB);
        if (dis == null) {
            dis = disMap.get(aiaB).get(aiaA);
        }
        return dis;
    }
    /**
     * 指定距离阈值的聚类算法
     *
     * @param threshold
     * @return
     */
    public abstract List<Cluster> startAnalysisByThreshold(double threshold);

    /**
     * 指定簇个数的聚类算法
     *
     * @param num
     * @return
     */
    public abstract List<Cluster> startAnalysisByClusterNumber(int num);

    public List<AiaProject> getBadAia() {
        return badAia;
    }

    public Map<AiaProject, Map<AiaProject, Double>> getDisMap(){
        return getDisMap();
    }

    public CalculateDistance getCalculator(){
        return calculator;
    }
}
