/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.viewmodels.common.extensions

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.sygic.maps.uikit.viewmodels.R
import com.sygic.sdk.map.MapRoadNumberFormat
import com.sygic.sdk.map.MapRoadNumberFormat.NumberShape
import com.sygic.sdk.map.MapRoadNumberFormat.SignColor
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo.SignElement.PictogramType
import com.sygic.sdk.navigation.routeeventnotifications.SignpostInfo.SignElement.SignElementType

@DrawableRes
internal fun SignpostInfo.pictogramDrawableRes(): Int =
    when (signElements.firstOrNull { it.elementType == SignElementType.Pictogram }?.pictogramType ?: 0) {
        PictogramType.Airport -> R.drawable.ic_pictogram_airport
        PictogramType.BusStation -> R.drawable.ic_pictogram_bus
        PictogramType.Fair -> R.drawable.ic_pictogram_fair
        PictogramType.FerryConnection -> R.drawable.ic_pictogram_ferry
        PictogramType.FirstAidPost -> R.drawable.ic_pictogram_er
        PictogramType.Harbour -> R.drawable.ic_pictogram_harbour
        PictogramType.Hospital -> R.drawable.ic_pictogram_er
        PictogramType.HotelOrMotel -> R.drawable.ic_pictogram_acc
        PictogramType.IndustrialArea -> R.drawable.ic_pictogram_factory
        PictogramType.InformationCenter -> R.drawable.ic_pictogram_info
        PictogramType.ParkingFacility -> R.drawable.ic_parking
        PictogramType.PetrolStation -> R.drawable.ic_pictogram_gas
        PictogramType.RailwayStation -> R.drawable.ic_pictogram_train
        PictogramType.RestArea -> R.drawable.ic_pictogram_rest
        PictogramType.Restaurant -> R.drawable.ic_pictogram_food
        PictogramType.Toilet -> R.drawable.ic_pictogram_wc
        else -> 0
    }

@DrawableRes
internal fun MapRoadNumberFormat.roadSignBackgroundDrawableRes(): Int = when (shape) {
    NumberShape.BlueShape1 -> R.drawable.ic_roadsign_are_blue
    NumberShape.GreenEShape3 -> R.drawable.ic_roadsign_are_green
    NumberShape.BlueNavyShape2 -> R.drawable.ic_roadsign_hun_blue
    NumberShape.GreenEShape2 -> R.drawable.ic_roadsign_hun_green
    NumberShape.GreenAShape4 -> R.drawable.ic_roadsign_aus_4_green
    NumberShape.RedShape5 -> R.drawable.ic_roadsign_nzl_red
    NumberShape.BlueShape5 -> R.drawable.ic_roadsign_aus_2_blue
    NumberShape.BlueShape6 -> R.drawable.ic_roadsign_deu_blue
    NumberShape.RedShape6 -> R.drawable.ic_roadsign_tur_red
    NumberShape.GreenEShape6 -> R.drawable.ic_roadsign_svn_green
    NumberShape.RedShape8 -> R.drawable.ic_roadsign_che_red
    NumberShape.RedShape9, NumberShape.RedShape10 -> R.drawable.ic_roadsign_fra_red
    NumberShape.BrownShape7 -> R.drawable.ic_roadsign_aus_1_brown
    NumberShape.BlackShape11WhiteBorder -> R.drawable.ic_roadsign_aus_4_black
    NumberShape.WhiteShape12BlueNavyBorder -> R.drawable.ic_roadsign_aus_5_white
    NumberShape.YellowShape13GreenABorder -> R.drawable.ic_roadsign_aus_5_green
    NumberShape.BlueRedCanShape -> R.drawable.ic_roadsign_can_blue_red_rebon
    NumberShape.WhiteShape14GreenEBorder -> R.drawable.ic_roadsign_can_white_green_border
    NumberShape.WhiteShape15BlackBorder -> R.drawable.ic_roadsign_can_white_black_border
    NumberShape.GreenEShape16WhiteBorder, NumberShape.GreenEShape18WhiteBorder -> R.drawable.ic_roadsign_ita_green
    NumberShape.BlueShape17WhiteBorder -> R.drawable.ic_roadsign_isr_blue
    NumberShape.WhiteShape17BlackBorder, NumberShape.WhiteShape24BlackBorder -> R.drawable.ic_roadsign_isr_white_black_border
    NumberShape.WhiteShape24BlueMexBorder -> R.drawable.ic_roadsign_isr_white_blue_border
    NumberShape.WhiteShape24RedBorder -> R.drawable.ic_roadsign_isr_white_red_border
    NumberShape.WhiteShape24GreenEBorder -> R.drawable.ic_roadsign_isr_white_green_border
    NumberShape.BlueShape18BlackBorder -> R.drawable.ic_roadsign_vnm_blue
    NumberShape.YellowShape18BlackBorder -> R.drawable.ic_roadsign_mys_yellow
    NumberShape.WhiteShape20BlackBorder -> R.drawable.ic_roadsign_nld_white
    NumberShape.USAShield -> R.drawable.ic_roadsign_us_1_blue
    NumberShape.WhiteShape22BlackBorder -> R.drawable.ic_roadsign_us_2_white
    NumberShape.WhiteShape21BlackBorder -> R.drawable.ic_roadsign_us_3_white
    NumberShape.OrangeShape23WhiteBorder -> R.drawable.ic_roadsign_hkg_orange
    NumberShape.BlueMexShape, NumberShape.RedMexShape -> R.drawable.ic_roadsign_mex_white
    NumberShape.GreenESauShape1 -> R.drawable.ic_roadsign_sau_1
    NumberShape.GreenESauShape2 -> R.drawable.ic_roadsign_sau_2
    NumberShape.GreenESauShape3 -> R.drawable.ic_roadsign_sau_3
    NumberShape.OrangeShape19BlackBorder -> R.drawable.ic_roadsign_fra_orange
    NumberShape.WhiteRect, NumberShape.WhiteRectBlackBorder -> R.drawable.ic_roadsign_rect_white
    NumberShape.WhiteRectGreenEBorder -> R.drawable.ic_roadsign_rect_white_green_border
    NumberShape.WhiteRectYellowBorder -> R.drawable.ic_roadsign_rect_white_yellow_border
    NumberShape.BlueRect, NumberShape.BlueNavyRect,
    NumberShape.BlueRectWhiteBorder, NumberShape.BlueNavyRectWhiteBorder -> R.drawable.ic_roadsign_rect_blue
    NumberShape.BlueRectBlackBorder -> R.drawable.ic_roadsign_rect_blue_black_border
    NumberShape.RedRect, NumberShape.RedRectWhiteBorder -> R.drawable.ic_roadsign_rect_red
    NumberShape.RedRectBlackBorder -> R.drawable.ic_roadsign_rect_red_black_border
    NumberShape.BrownRect -> R.drawable.ic_roadsign_rect_brown
    NumberShape.OrangeRect -> R.drawable.ic_roadsign_rect_orange
    NumberShape.YellowRect, NumberShape.YellowRectWhiteBorder -> R.drawable.ic_roadsign_rect_yellow
    NumberShape.YellowRectBlackBorder -> R.drawable.ic_roadsign_rect_yellow_black_border
    NumberShape.GreenERectBlackBorder -> R.drawable.ic_roadsign_rect_green_black_border
    NumberShape.GreenARect, NumberShape.GreenERect, NumberShape.GreenERectWhiteBorder,
    NumberShape.GreenARectGreenEBorder, NumberShape.Unknown -> R.drawable.ic_roadsign_rect_green
    else -> R.drawable.ic_roadsign_rect_green
}

@ColorRes
internal fun MapRoadNumberFormat.roadSignForegroundColorRes(): Int = when (numberColor) {
    SignColor.Black -> R.color.roadSignStroke
    SignColor.GreenE, SignColor.GreenA -> R.color.roadSignGreen
    SignColor.Blue, SignColor.BlueNavy, SignColor.BlueMex -> R.color.roadSignBlue
    SignColor.Red -> R.color.roadSignRed
    SignColor.Yellow -> R.color.roadSignYellow
    SignColor.Orange -> R.color.roadSignOrange
    SignColor.Brown -> R.color.roadSignBrown
    SignColor.White, SignColor.Unknown -> R.color.white
    else -> R.color.white
}