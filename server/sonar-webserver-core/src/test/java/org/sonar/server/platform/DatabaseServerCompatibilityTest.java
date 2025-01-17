/*
 * SonarQube
 * Copyright (C) 2009-2023 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform;

import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;
import org.sonar.server.platform.db.migration.version.DatabaseVersion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseServerCompatibilityTest {

  @Rule
  public LogTester logTester = new LogTester();

  @Test
  public void fail_if_requires_downgrade() {
    DatabaseVersion version = mock(DatabaseVersion.class);
    when(version.getStatus()).thenReturn(DatabaseVersion.Status.REQUIRES_DOWNGRADE);
    var compatibility = new DatabaseServerCompatibility(version);
    assertThatThrownBy(compatibility::start)
      .isInstanceOf(MessageException.class)
      .hasMessage("Database was upgraded to a more recent version of SonarQube. "
        + "A backup must probably be restored or the DB settings are incorrect.");
  }

  @Test
  public void fail_if_requires_firstly_to_upgrade_to_lts() {
    DatabaseVersion version = mock(DatabaseVersion.class);
    when(version.getStatus()).thenReturn(DatabaseVersion.Status.REQUIRES_UPGRADE);
    when(version.getVersion()).thenReturn(Optional.of(12L));
    var compatibility = new DatabaseServerCompatibility(version);
    assertThatThrownBy(compatibility::start)
      .isInstanceOf(MessageException.class)
      .hasMessage("The version of SonarQube is too old. Please upgrade to the Long Term Support version first.");
  }

  @Test
  public void log_warning_if_requires_upgrade() {
    DatabaseVersion version = mock(DatabaseVersion.class);
    when(version.getStatus()).thenReturn(DatabaseVersion.Status.REQUIRES_UPGRADE);
    when(version.getVersion()).thenReturn(Optional.of(DatabaseVersion.MIN_UPGRADE_VERSION));
    new DatabaseServerCompatibility(version).start();

    assertThat(logTester.logs()).hasSize(4);
    assertThat(logTester.logs(LoggerLevel.WARN)).contains(
      "The database must be manually upgraded. Please backup the database and browse /setup. "
        + "For more information: https://docs.sonarqube.org/latest/setup/upgrading",
      "################################################################################",
      "The database must be manually upgraded. Please backup the database and browse /setup. "
        + "For more information: https://docs.sonarqube.org/latest/setup/upgrading",
      "################################################################################");
  }

  @Test
  public void do_nothing_if_up_to_date() {
    DatabaseVersion version = mock(DatabaseVersion.class);
    when(version.getStatus()).thenReturn(DatabaseVersion.Status.UP_TO_DATE);
    new DatabaseServerCompatibility(version).start();
    // no error
  }
}
