/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.dashdocset;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public enum DashProvider
{
	Dash(DashIcons.Dash),
	Zeal(DashIcons.Zeal)
			{
				@Override
				public void open(@NotNull String[] keywords, @NotNull String query) throws Exception
				{
					StringBuilder builder = new StringBuilder();
					builder.append(StringUtil.join(keywords, ","));
					builder.append(":").append(query);

					final GeneralCommandLine commandLine = new GeneralCommandLine("zeal");
					commandLine.addParameter("--query");
					commandLine.addParameter(builder.toString());
					commandLine.createProcess();
				}
			},
	Velocity(DashIcons.Velocity);

	private final Icon myIcon;

	DashProvider(Icon icon)
	{
		myIcon = icon;
	}

	public Icon getIcon()
	{
		return myIcon;
	}

	public void open(@NotNull String[] keywords, @NotNull String query) throws Exception
	{
		StringBuilder builder = new StringBuilder("dash-plugin://");
		builder.append("keys=").append(StringUtil.join(keywords, ","));
		builder.append("&").append("query=").append(URLEncoder.encode(query, "UTF-8").replace("+", "%20"));

		if(Desktop.isDesktopSupported())
		{
			Desktop.getDesktop().browse(new URI(builder.toString()));
		}
	}

	@NotNull
	public static DashProvider getProvider()
	{
		if(SystemInfo.isWindows)
		{
			return Velocity;
		}
		else if(SystemInfo.isLinux)
		{
			return Zeal;
		}
		else if(SystemInfo.isMac)
		{
			return Dash;
		}
		else
		{
			throw new UnsupportedOperationException("OS is not supported " + SystemInfo.OS_NAME);
		}
	}
}
