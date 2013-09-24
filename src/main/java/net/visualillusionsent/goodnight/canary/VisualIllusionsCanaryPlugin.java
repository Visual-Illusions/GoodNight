package net.visualillusionsent.goodnight.canary;

import net.canarymod.plugin.Plugin;
import net.visualillusionsent.utils.ProgramStatus;
import net.visualillusionsent.utils.VersionChecker;

/**
 * Visual Illusions Canary Plugin extension
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsCanaryPlugin extends Plugin {

    private final VersionChecker vc;

    public VisualIllusionsCanaryPlugin() {
        this.vc = new VersionChecker(getName(), getVersion(), getBuild(), getVersionCheckURL(), getStatus(), false);
    }

    protected final void checkStatus() {
        String statusReport = "%s has decleared itself as '%s' build. %s";
        switch (this.getStatus()) {
            case UNKNOWN:
                getLogman().severe(String.format(statusReport, getName(), "UNKNOWN STATUS", "Use is not advised and could cause damage to your system!"));
                break;
            case ALPHA:
                getLogman().severe(String.format(statusReport, getName(), "ALPHA", "Production use is not advised!"));
                break;
            case BETA:
                getLogman().severe(String.format(statusReport, getName(), "BETA", "Production use is not advised!"));
                break;
            case RELEASE_CANDIDATE:
                getLogman().severe(String.format(statusReport, getName(), "RELEASE CANDIDATE", "Expect some bugs."));
                break;
        }
    }

    protected final void checkVersion() {
        Boolean islatest = vc.isLatest();
        if (islatest == null) {
            getLogman().warning("VersionCheckerError: " + vc.getErrorMessage());
        }
        else if (!islatest) {
            getLogman().warning(vc.getUpdateAvailibleMessage());
            getLogman().warning(String.format("You can view update info @ %s#ChangeLog", getWikiURL()));
        }
    }

    final ProgramStatus getStatus() {
        try {
            return ProgramStatus.valueOf(getCanaryInf().getString("program.status").toUpperCase());
        }
        catch (Exception ex) {
            return ProgramStatus.UNKNOWN;
        }
    }

    final String getBuild() {
        return getCanaryInf().getString("build.number", "0");
    }

    final String getBuildTime() {
        return getCanaryInf().getString("build.time", "19700101-0000");
    }

    final VersionChecker getVersionChecker() {
        return vc;
    }

    final String getVersionCheckURL() {
        return getCanaryInf().getString("version.check.url", "missing.url");
    }

    final String getWikiURL() {
        return getCanaryInf().getString("wiki.url", "missing.url");
    }

    final String getIssuesURL() {
        return getCanaryInf().getString("issues.url", "missing.url");
    }

    final String getDevelopers() {
        return getCanaryInf().getString("developers", "missing.developers");
    }
}
