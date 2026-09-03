package com.meusim.application.modules.identity.base.user.query;

import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import java.util.UUID;

public interface ResponsibleSnapshotQuery {
    ResponsibleSnapshotDTO findResponsibleSnapshotByUserId(UUID userId);
}
