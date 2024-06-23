package com.example.Game;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.management.monitor.GaugeMonitor;

import org.json.JSONObject;

import com.example.Piece.Piece;

import javafx.util.Pair;

import org.asynchttpclient.*;

public class Api_call {
    /* public static void main(String[] args) {
        List<String> fenAndMoves = getFenAndMoves("8/pp2kppp/4p3/6P1/P1n5/2r5/1r5P/K4R2 w KQkq - - 0 1");
        System.out.println(fenAndMoves);
    } */

    public static ArrayList<String> getFenAndMoves(String fen) {
            ArrayList<String> fenAndMoves = new ArrayList<>();
            try {

                String encodedFEN = URLEncoder.encode(fen, "UTF-8");
        
                String apiUrl = "https://chess-stockfish-16-api.p.rapidapi.com/chess/api";

                AsyncHttpClient client = new DefaultAsyncHttpClient();
        
                String responseBody = client.preparePost(apiUrl)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("X-RapidAPI-Key", "6a1ed3e927msh2d8d4c93672832dp14f057jsn63c9b950ae4f")
                    .addHeader("X-RapidAPI-Host", "chess-stockfish-16-api.p.rapidapi.com")
                    .setBody("fen=" + encodedFEN)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(Response::getResponseBody)
                    .join();
        
                JSONObject json = new JSONObject(responseBody);
                System.out.println(fen);
                System.out.println(json.toString());
                String bestMove = "";
                bestMove = json.getString("bestmove");
                
                fenAndMoves.add(fen);
                fenAndMoves.add(bestMove);
                
                // Close the client
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fenAndMoves;
        }
}
