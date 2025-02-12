package org.jellyfin.playback.jellyfin

import org.jellyfin.playback.core.plugin.playbackPlugin
import org.jellyfin.playback.jellyfin.playsession.PlaySessionService
import org.jellyfin.playback.jellyfin.playsession.PlaySessionSocketService
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.sockets.SocketInstance

fun jellyfinPlugin(
	api: ApiClient,
	socketInstance: SocketInstance,
) = playbackPlugin {
	provide(PlaySessionService(api))
	provide(PlaySessionSocketService(socketInstance))
}
