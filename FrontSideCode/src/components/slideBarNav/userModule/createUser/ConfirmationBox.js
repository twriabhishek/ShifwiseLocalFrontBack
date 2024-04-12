import {
  Box,
  Button,
  Dialog,
  DialogContent,
  Grid,
  IconButton,
  Typography,
} from "@mui/material";

import { ToastContainer } from "react-bootstrap";
import CloseIcon from "@mui/icons-material/Close";
import { ClipLoader } from "react-spinners";

function ConfirmationBox({
  open,
  openModal,
  setOpenModal,
  handleClose,
  closeDialog,
  handleDialogClose,
  onClose,
  title,
  deletefunction,
  diffToast,
  isLoading,
  setIsLoading,
  ...other
}) {
  return (
    <div>
      <Dialog
        fullWidth
        open={openModal}
        maxWidth="xs"
        scroll="body"
        onClose={closeDialog}
        disableAutoFocus={true}
      >
        <DialogContent sx={{ px: 8, py: 6, position: "relative" }}>
          <IconButton
            aria-label="close"
            size="medium"
            onClick={closeDialog}
            sx={{
              position: "absolute",
              right: "1rem",
              top: "1rem",
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
          <Grid container spacing={6}>
            <Grid item xs={12}>
              <Box>
                <Typography variant="h6" className="text-center">
                  Delete {title}
                </Typography>
                <Typography
                  variant="body1"
                  className="text-center"
                  sx={{ marginTop: "6px" }}
                >
                  Are you sure want to delete {title}?
                </Typography>
              </Box>
            </Grid>

            <Grid item xs={12} className="d-flex justify-content-center">
              <Button
                color="error"
                onClick={() => {
                  deletefunction();
                }}
                size="medium"
                id="UserModuleBtn"
              >
                Delete
                <div style={{ display: "flex", alignItems: "center" }}>
                  {isLoading && (
                    <ClipLoader color="white" loading={isLoading} size={20} />
                  )}
                </div>
              </Button>
              <ToastContainer />
            </Grid>
          </Grid>
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default ConfirmationBox;
