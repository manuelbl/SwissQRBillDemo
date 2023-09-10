//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Box, Button, Divider, Paper } from "@mui/material";
import Grid from '@mui/material/Unstable_Grid2';
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ExampleList } from "./example-list";
import { QrBill } from "../qrbill-api/qrbill";

interface ExampleProps {
  selectBill: (bill: QrBill) => void;
}

/**
 * Page displaying QR bill examples
 */
const Examples = ({ selectBill }: ExampleProps) => {

  const navigate = useNavigate();

  const { t } = useTranslation();

  /**
   * Select the QR bill and go to edit page
   * @param bill QR bill
   */
  function onBillSelected(bill: QrBill): void {
    selectBill(bill);
    navigate('/');
  }

  return (
    <Box>
      {ExampleList.map((ex, index) => {
        return (
          <Paper key={ex.billID} sx={{marginTop: '1rem'}}>
            <img className="qr-bill" src={`/qrbill-api/bill/image/${ex.billID}`} alt={`${t('qrbill_example', { num: index + 1})}`} />
            <Divider />
            <Grid container justifyContent='end'>
              <Grid sx={{padding: '0.5rem 1rem'}}>
                <Button variant="contained" onClick={() => onBillSelected(ex.bill)}>{t('select')}</Button>
              </Grid>
            </Grid>
          </Paper>
        )
      })}
    </Box>
  );
}

export default Examples;
