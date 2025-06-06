package org.cbioportal.legacy.web.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.cbioportal.legacy.model.DataBin;
import org.springframework.stereotype.Component;

@Component
public class DiscreteDataBinner {

  public List<DataBin> calculateDataBins(List<BigDecimal> values, Set<BigDecimal> uniqueValues) {
    List<DataBin> dataBins = initDataBins(uniqueValues);

    DataBinHelper.calcCounts(dataBins, values);

    return dataBins;
  }

  public List<DataBin> initDataBins(Set<BigDecimal> uniqueValues) {
    return uniqueValues.stream()
        .map(
            d -> {
              DataBin dataBin = new DataBin();

              dataBin.setCount(0);

              // set both start and end to the same value
              dataBin.setStart(d);
              dataBin.setEnd(d);

              return dataBin;
            })
        .collect(Collectors.toList());
  }
}
