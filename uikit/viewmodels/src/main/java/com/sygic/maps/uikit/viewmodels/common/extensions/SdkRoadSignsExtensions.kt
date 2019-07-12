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
import com.sygic.maps.uikit.views.common.extensions.NO_ID
import com.sygic.sdk.navigation.warnings.NaviSignInfo

@DrawableRes
internal fun NaviSignInfo.pictogramDrawableRes(): Int {
    val pictogramType =
        signElements.firstOrNull { signElement -> signElement.elementType == NaviSignInfo.SignElement.SignElementType.Pictogram }?.pictogramType
            ?: return NO_ID

    return when (pictogramType) {
        NaviSignInfo.SignElement.PictogramType.Airport -> R.drawable.ic_pictogram_airport
        NaviSignInfo.SignElement.PictogramType.BusStation -> R.drawable.ic_pictogram_bus
        NaviSignInfo.SignElement.PictogramType.Fair -> R.drawable.ic_pictogram_fair
        NaviSignInfo.SignElement.PictogramType.FerryConnection -> R.drawable.ic_pictogram_ferry
        NaviSignInfo.SignElement.PictogramType.FirstAidPost -> R.drawable.ic_pictogram_er
        NaviSignInfo.SignElement.PictogramType.Harbour -> R.drawable.ic_pictogram_harbour
        NaviSignInfo.SignElement.PictogramType.Hospital -> R.drawable.ic_pictogram_er
        NaviSignInfo.SignElement.PictogramType.HotelOrMotel -> R.drawable.ic_pictogram_acc
        NaviSignInfo.SignElement.PictogramType.IndustrialArea -> R.drawable.ic_pictogram_factory
        NaviSignInfo.SignElement.PictogramType.InformationCenter -> R.drawable.ic_pictogram_info
        NaviSignInfo.SignElement.PictogramType.ParkingFacility -> R.drawable.ic_parking
        NaviSignInfo.SignElement.PictogramType.PetrolStation -> R.drawable.ic_pictogram_gas
        NaviSignInfo.SignElement.PictogramType.RailwayStation -> R.drawable.ic_pictogram_train
        NaviSignInfo.SignElement.PictogramType.RestArea -> R.drawable.ic_pictogram_rest
        NaviSignInfo.SignElement.PictogramType.Restaurant -> R.drawable.ic_pictogram_food
        NaviSignInfo.SignElement.PictogramType.Toilet -> R.drawable.ic_pictogram_wc
        else -> NO_ID
    }
}

@DrawableRes
internal fun NaviSignInfo.RouteNumberFormat.backgroundDrawableRes(): Int = when (shape) {
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueShape1 -> R.drawable.ic_roadsign_are_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenEShape3 -> R.drawable.ic_roadsign_are_green
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueNavyShape2 -> R.drawable.ic_roadsign_hun_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenEShape2 -> R.drawable.ic_roadsign_hun_green
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenAShape4 -> R.drawable.ic_roadsign_aus_4_green
    NaviSignInfo.RouteNumberFormat.NumberShape.RedShape5 -> R.drawable.ic_roadsign_nzl_red
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueShape5 -> R.drawable.ic_roadsign_aus_2_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueShape6 -> R.drawable.ic_roadsign_deu_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.RedShape6 -> R.drawable.ic_roadsign_tur_red
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenEShape6 -> R.drawable.ic_roadsign_svn_green
    NaviSignInfo.RouteNumberFormat.NumberShape.RedShape8 -> R.drawable.ic_roadsign_che_red
    NaviSignInfo.RouteNumberFormat.NumberShape.RedShape9, NaviSignInfo.RouteNumberFormat.NumberShape.RedShape10 -> R.drawable.ic_roadsign_fra_red
    NaviSignInfo.RouteNumberFormat.NumberShape.BrownShape7 -> R.drawable.ic_roadsign_aus_1_brown
    NaviSignInfo.RouteNumberFormat.NumberShape.BlackShape11WhiteBorder -> R.drawable.ic_roadsign_aus_4_black
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape12BlueNavyBorder -> R.drawable.ic_roadsign_aus_5_white
    NaviSignInfo.RouteNumberFormat.NumberShape.YellowShape13GreenABorder -> R.drawable.ic_roadsign_aus_5_green
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueRedCanShape -> R.drawable.ic_roadsign_can_blue_red_rebon
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape14GreenEBorder -> R.drawable.ic_roadsign_can_white_green_border
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape15BlackBorder -> R.drawable.ic_roadsign_can_white_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenEShape16WhiteBorder, NaviSignInfo.RouteNumberFormat.NumberShape.GreenEShape18WhiteBorder -> R.drawable.ic_roadsign_ita_green
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueShape17WhiteBorder -> R.drawable.ic_roadsign_isr_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape17BlackBorder, NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape24BlackBorder -> R.drawable.ic_roadsign_isr_white_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape24BlueMexBorder -> R.drawable.ic_roadsign_isr_white_blue_border
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape24RedBorder -> R.drawable.ic_roadsign_isr_white_red_border
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape24GreenEBorder -> R.drawable.ic_roadsign_isr_white_green_border
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueShape18BlackBorder -> R.drawable.ic_roadsign_vnm_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.YellowShape18BlackBorder -> R.drawable.ic_roadsign_mys_yellow
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape20BlackBorder -> R.drawable.ic_roadsign_nld_white
    NaviSignInfo.RouteNumberFormat.NumberShape.USAShield -> R.drawable.ic_roadsign_us_1_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape22BlackBorder -> R.drawable.ic_roadsign_us_2_white
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteShape21BlackBorder -> R.drawable.ic_roadsign_us_3_white
    NaviSignInfo.RouteNumberFormat.NumberShape.OrangeShape23WhiteBorder -> R.drawable.ic_roadsign_hkg_orange
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueMexShape, NaviSignInfo.RouteNumberFormat.NumberShape.RedMexShape -> R.drawable.ic_roadsign_mex_white
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenESauShape1 -> R.drawable.ic_roadsign_sau_1
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenESauShape2 -> R.drawable.ic_roadsign_sau_2
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenESauShape3 -> R.drawable.ic_roadsign_sau_3
    NaviSignInfo.RouteNumberFormat.NumberShape.OrangeShape19BlackBorder -> R.drawable.ic_roadsign_fra_orange
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteRect, NaviSignInfo.RouteNumberFormat.NumberShape.WhiteRectBlackBorder -> R.drawable.ic_roadsign_rect_white
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteRectGreenEBorder -> R.drawable.ic_roadsign_rect_white_green_border
    NaviSignInfo.RouteNumberFormat.NumberShape.WhiteRectYellowBorder -> R.drawable.ic_roadsign_rect_white_yellow_border
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueRect, NaviSignInfo.RouteNumberFormat.NumberShape.BlueNavyRect,
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueRectWhiteBorder, NaviSignInfo.RouteNumberFormat.NumberShape.BlueNavyRectWhiteBorder -> R.drawable.ic_roadsign_rect_blue
    NaviSignInfo.RouteNumberFormat.NumberShape.BlueRectBlackBorder -> R.drawable.ic_roadsign_rect_blue_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.RedRect, NaviSignInfo.RouteNumberFormat.NumberShape.RedRectWhiteBorder -> R.drawable.ic_roadsign_rect_red
    NaviSignInfo.RouteNumberFormat.NumberShape.RedRectBlackBorder -> R.drawable.ic_roadsign_rect_red_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.BrownRect -> R.drawable.ic_roadsign_rect_brown
    NaviSignInfo.RouteNumberFormat.NumberShape.OrangeRect -> R.drawable.ic_roadsign_rect_orange
    NaviSignInfo.RouteNumberFormat.NumberShape.YellowRect, NaviSignInfo.RouteNumberFormat.NumberShape.YellowRectWhiteBorder -> R.drawable.ic_roadsign_rect_yellow
    NaviSignInfo.RouteNumberFormat.NumberShape.YellowRectBlackBorder -> R.drawable.ic_roadsign_rect_yellow_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenERectBlackBorder -> R.drawable.ic_roadsign_rect_green_black_border
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenARect, NaviSignInfo.RouteNumberFormat.NumberShape.GreenERect, NaviSignInfo.RouteNumberFormat.NumberShape.GreenERectWhiteBorder,
    NaviSignInfo.RouteNumberFormat.NumberShape.GreenARectGreenEBorder, NaviSignInfo.RouteNumberFormat.NumberShape.Unknown -> R.drawable.ic_roadsign_rect_green
    else -> R.drawable.ic_roadsign_rect_green
}

@ColorRes
internal fun NaviSignInfo.RouteNumberFormat.foregroundColorRes(): Int = when (numberColor) {
    NaviSignInfo.RouteNumberFormat.SignColor.Black -> R.color.roadSignStroke
    NaviSignInfo.RouteNumberFormat.SignColor.GreenE, NaviSignInfo.RouteNumberFormat.SignColor.GreenA -> R.color.roadSignGreen
    NaviSignInfo.RouteNumberFormat.SignColor.Blue, NaviSignInfo.RouteNumberFormat.SignColor.BlueNavy, NaviSignInfo.RouteNumberFormat.SignColor.BlueMex -> R.color.roadSignBlue
    NaviSignInfo.RouteNumberFormat.SignColor.Red -> R.color.roadSignRed
    NaviSignInfo.RouteNumberFormat.SignColor.Yellow -> R.color.roadSignYellow
    NaviSignInfo.RouteNumberFormat.SignColor.Orange -> R.color.roadSignOrange
    NaviSignInfo.RouteNumberFormat.SignColor.Brown -> R.color.roadSignBrown
    NaviSignInfo.RouteNumberFormat.SignColor.White, NaviSignInfo.RouteNumberFormat.SignColor.Unknown -> R.color.white
    else -> R.color.white
}