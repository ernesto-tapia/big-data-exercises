/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author Ernesto
 */
public class MovieRecommender {
    private int totalReviews = 0, totalUsers = 0, totalProducts=0; 
        private UserBasedRecommender recommender;
        private HashMap<String, Integer> users =new HashMap();
        private Hashtable<String, Integer> products = new Hashtable<>();
        
        public MovieRecommender(String path) throws IOException, TasteException{
        start(path);    
    }
        private void start(String path) throws FileNotFoundException, IOException, TasteException {
        String pathWriter=  "src/test/java/nearsoft/academy/bigdata/recommendation/movies.csv";   
        String userId="", productId= "", score, line;
        int currentUser=0, currentProduct =0;
        
        File file = new File(path);
        BufferedWriter writer;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            writer = new BufferedWriter(new FileWriter(pathWriter));
            while((line =reader.readLine()) !=null){
                String[] part=line.split(": ");
                switch(part[0]){
                    case "product/productId":
                        productId = part[1];
                        if (!products.containsKey(productId)) {
                            totalProducts++;
                            products.put(productId,totalProducts);
                            currentProduct = totalProducts;
                        }else{
                            currentProduct = products.get(productId);
                        }
                        break;
                    case "review/userId":
                        userId = part[1];
                        if (!users.containsKey(userId)) {
                            totalUsers++;
                            users.put(userId,totalUsers);
                            currentUser = totalUsers;
                        }else{
                            currentUser = users.get(userId);
                        }
                        break;
                    case "review/score":
                        score = part[1];
                        writer.write(currentUser+","+currentProduct+","+score+"\n");
                        totalReviews++;
                        break;  
                }
            }
        reader.close();
        }
        writer.close();
        DataModel model = new FileDataModel(new File(pathWriter));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood group = new ThresholdUserNeighborhood(0.1, similarity, model);
        recommender = new GenericUserBasedRecommender(model, group, similarity);
        }
    public int getTotalReviews() {
        return totalReviews;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalUsers() {
        return totalUsers;
    }
    public List<String> getRecommendationsForUser(String userId) throws IOException, TasteException{
        List<String> list = new ArrayList<String>();
        int id= users.get(userId);
        List<RecommendedItem> recommendations = recommender.recommend(id, 3);
        for(RecommendedItem recommendation: recommendations){
            list.add(getProductName((int)recommendation.getItemID()));
        }
        return list;
    }
    public String getProductName(int value){
    Enumeration e = products.keys();
    while (e.hasMoreElements()){
        String key = (String) e.nextElement();
        if (products.get(key)==value){
            return key;
        }
    }
    return null;
    }
}
