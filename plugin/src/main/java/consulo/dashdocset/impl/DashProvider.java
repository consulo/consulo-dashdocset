/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dashdocset.impl;

import consulo.dashdocset.icon.DashIconGroup;
import consulo.platform.Platform;
import consulo.platform.PlatformOperatingSystem;
import consulo.process.ProcessHandlerBuilder;
import consulo.process.cmd.GeneralCommandLine;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;

import java.net.URLEncoder;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public enum DashProvider {
    Dash(DashIconGroup.dash()),
    Zeal(DashIconGroup.zeal()) {
        @Override
        public void open(@Nonnull String[] keywords, @Nonnull String query) throws Exception {
            StringBuilder builder = new StringBuilder();
            builder.append(StringUtil.join(keywords, ","));
            builder.append(":").append(query);

            final GeneralCommandLine commandLine = new GeneralCommandLine("zeal");
            commandLine.addParameter("--query");
            commandLine.addParameter(builder.toString());
            ProcessHandlerBuilder.create(commandLine).build();
        }
    },
    Velocity(DashIconGroup.velocity());

    private final Image myIcon;

    DashProvider(Image icon) {
        myIcon = icon;
    }

    public Image getIcon() {
        return myIcon;
    }

    public void open(@Nonnull String[] keywords, @Nonnull String query) throws Exception {
        StringBuilder builder = new StringBuilder("dash-plugin://");
        builder.append("keys=").append(StringUtil.join(keywords, ","));
        builder.append("&").append("query=").append(URLEncoder.encode(query, "UTF-8").replace("+", "%20"));

        Platform.current().openInBrowser(builder.toString());
    }

    @Nonnull
    public static DashProvider getProvider() {
        PlatformOperatingSystem os = Platform.current().os();
        if (os.isWindows()) {
            return Velocity;
        }
        else if (os.isLinux()) {
            return Zeal;
        }
        else if (os.isMac()) {
            return Dash;
        }
        else {
            throw new UnsupportedOperationException("OS is not supported " + os.name());
        }
    }
}
