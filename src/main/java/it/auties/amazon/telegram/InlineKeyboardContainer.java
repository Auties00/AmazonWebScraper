package it.auties.amazon.telegram;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record InlineKeyboardContainer(@SerializedName("inline_keyboard") List<List<InlineKeyboard>> keyboard){

}